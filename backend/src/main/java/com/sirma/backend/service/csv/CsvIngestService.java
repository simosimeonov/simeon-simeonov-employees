package com.sirma.backend.service.csv;


import com.sirma.backend.api.dto.InvalidRowDto;
import com.sirma.backend.config.AppProperties;
import com.sirma.backend.domain.ParseOutcome;
import com.sirma.backend.domain.Period;
import com.sirma.backend.domain.ProjectMap;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CsvIngestService {

    private static final Logger log = LoggerFactory.getLogger(CsvIngestService.class);
    private static final int BOM = 0xFEFF;
    private static final int BATCH_SIZE = 256;
    private static final RawRow[] POISON = new RawRow[0];

    private final CsvRowParser parser;
    private final AppProperties props;
    private final ExecutorService executor;

    public CsvIngestService(CsvRowParser parser,
                            AppProperties props,
                            @Qualifier("projectExecutor") ExecutorService executor) {
        this.parser = parser;
        this.props = props;
        this.executor = executor;
    }

    public IngestResult ingest(InputStream in) {
        long start = System.currentTimeMillis();

        ProjectMap projectMap = new ProjectMap();
        Queue<InvalidRowDto> invalidQueue = new ConcurrentLinkedQueue<>();
        AtomicLong validCount = new AtomicLong();
        AtomicLong totalRows = new AtomicLong();

        int workers = Math.max(1, Runtime.getRuntime().availableProcessors());
        BlockingQueue<RawRow[]> queue = new ArrayBlockingQueue<>(workers * 4);

        List<Future<?>> workerFutures = new ArrayList<>(workers);
        for (int i = 0; i < workers; i++) {
            workerFutures.add(executor.submit(() ->
                    runWorker(queue, projectMap, invalidQueue, validCount)));
        }

        try {
            readAndDispatch(in, queue, totalRows);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Ingest interrupted while reading CSV", e);
        } finally {
            sendPoison(queue, workers);
        }

        awaitAll(workerFutures);

        List<InvalidRowDto> invalid = new ArrayList<>(invalidQueue);
        invalid.sort(Comparator.comparingLong(InvalidRowDto::lineNumber));

        long elapsed = System.currentTimeMillis() - start;
        log.info("CSV ingest: totalRows={}, validRows={}, invalidRows={}, projects={}, elapsedMs={}",
                totalRows.get(), validCount.get(), invalid.size(), projectMap.size(), elapsed);

        return new IngestResult(projectMap, invalid, totalRows.get(), validCount.get());
    }

    private void readAndDispatch(InputStream in,
                                 BlockingQueue<RawRow[]> queue,
                                 AtomicLong totalRows) throws InterruptedException {
        char delimiter = props.csv().delimiter().isEmpty() ? ',' : props.csv().delimiter().charAt(0);
        CSVFormat.Builder formatBuilder = CSVFormat.DEFAULT.builder()
                .setDelimiter(delimiter)
                .setIgnoreSurroundingSpaces(true);
        if (props.csv().hasHeader()) {
            formatBuilder.setHeader().setSkipHeaderRecord(true);
        }
        CSVFormat format = formatBuilder.build();

        try (Reader reader = stripBom(new InputStreamReader(in, StandardCharsets.UTF_8));
             CSVParser csv = CSVParser.parse(reader, format)) {
            RawRow[] batch = new RawRow[BATCH_SIZE];
            int idx = 0;
            for (CSVRecord r : csv) {
                batch[idx++] = new RawRow(r.getRecordNumber(), r.values());
                totalRows.incrementAndGet();
                if (idx == BATCH_SIZE) {
                    queue.put(batch);
                    batch = new RawRow[BATCH_SIZE];
                    idx = 0;
                }
            }
            if (idx > 0) {
                RawRow[] tail = new RawRow[idx];
                System.arraycopy(batch, 0, tail, 0, idx);
                queue.put(tail);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read CSV", e);
        }
    }

    private void runWorker(BlockingQueue<RawRow[]> queue,
                           ProjectMap projectMap,
                           Queue<InvalidRowDto> invalidQueue,
                           AtomicLong validCount) {
        try {
            while (true) {
                RawRow[] batch = queue.take();
                if (batch == POISON) {
                    return;
                }
                processBatch(batch, projectMap, invalidQueue, validCount);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void processBatch(RawRow[] batch,
                              ProjectMap projectMap,
                              Queue<InvalidRowDto> invalidQueue,
                              AtomicLong validCount) {
        for (RawRow row : batch) {
            ParseOutcome outcome = parser.parseRow(row.lineNumber(), row.columns());
            switch (outcome) {
                case ParseOutcome.Valid v -> {
                    projectMap.add(v.projectId(), v.userId(), new Period(v.dateFrom(), v.dateTo()));
                    validCount.incrementAndGet();
                }
                case ParseOutcome.Invalid bad -> invalidQueue.add(
                        new InvalidRowDto(bad.lineNumber(), bad.rawLine(), bad.reason()));
            }
        }
    }

    private static void sendPoison(BlockingQueue<RawRow[]> queue, int count) {
        for (int i = 0; i < count; i++) {
            try {
                queue.put(POISON);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private static void awaitAll(List<Future<?>> futures) {
        for (Future<?> f : futures) {
            try {
                f.get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Ingest interrupted", e);
            } catch (ExecutionException e) {
                throw new RuntimeException("Ingest worker failed", e.getCause());
            }
        }
    }

    private static Reader stripBom(Reader source) throws IOException {
        PushbackReader pb = new PushbackReader(new BufferedReader(source), 1);
        int ch = pb.read();
        if (ch != -1 && ch != BOM) {
            pb.unread(ch);
        }
        return pb;
    }

    private record RawRow(long lineNumber, String[] columns) {}

    public record IngestResult(ProjectMap projectMap, List<InvalidRowDto> invalidRows,
                               long totalRows, long validRows) {}
}