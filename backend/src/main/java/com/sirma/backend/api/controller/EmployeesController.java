package com.sirma.backend.api.controller;

import com.sirma.backend.api.dto.PairResultDto;
import com.sirma.backend.api.dto.StatsDto;
import com.sirma.backend.api.dto.UploadResponse;
import com.sirma.backend.api.mapper.ResultMapper;
import com.sirma.backend.domain.PairAggregate;
import com.sirma.backend.service.calculation.PairFinderService;
import com.sirma.backend.service.csv.CsvIngestService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeesController {

    private final CsvIngestService ingest;
    private final PairFinderService finder;
    private final ResultMapper mapper;

    public EmployeesController(CsvIngestService ingest, PairFinderService finder, ResultMapper mapper) {
        this.ingest = ingest;
        this.finder = finder;
        this.mapper = mapper;
    }

    @PostMapping("/upload")
    public UploadResponse upload(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        long start = System.currentTimeMillis();
        CsvIngestService.IngestResult r;
        try {
            r = ingest.ingest(file.getInputStream());
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read uploaded file", e);
        }
        List<PairAggregate> aggregates = finder.findLongestPairs(r.projectMap());
        List<PairResultDto> top = mapper.toDto(aggregates);
        long elapsed = System.currentTimeMillis() - start;
        StatsDto stats = new StatsDto(
                r.totalRows(),
                r.validRows(),
                r.invalidRows().size(),
                r.projectMap().size(),
                elapsed);
        return new UploadResponse(top, r.invalidRows(), stats);
    }
}
