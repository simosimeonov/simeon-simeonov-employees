package com.sirma.backend.service.calculation;

import com.sirma.backend.domain.OverlapResult;
import com.sirma.backend.domain.PairAggregate;
import com.sirma.backend.domain.PairKey;
import com.sirma.backend.domain.ProjectMap;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Service
public class PairFinderService {

    private final OverlapCalculator calculator;
    private final ExecutorService projectExecutor;

    public PairFinderService(OverlapCalculator calculator,
                             @Qualifier("projectExecutor") ExecutorService projectExecutor) {
        this.calculator = calculator;
        this.projectExecutor = projectExecutor;
    }

    public List<PairAggregate> findLongestPairs(ProjectMap pm) {
        if (pm == null || pm.size() == 0) {
            return List.of();
        }

        List<Future<Map<PairKey, List<OverlapResult>>>> futures = new ArrayList<>();
        for (Integer projectId : pm.projectIds()) {
            futures.add(projectExecutor.submit(
                    new ProjectPairsTask(projectId, pm.get(projectId), calculator)));
        }

        List<PairAggregate> candidates = new ArrayList<>();
        long max = 0L;

        for (Future<Map<PairKey, List<OverlapResult>>> f : futures) {
            Map<PairKey, List<OverlapResult>> part;
            try {
                part = f.get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Pair calculation interrupted", e);
            } catch (ExecutionException e) {
                throw new RuntimeException("Pair calculation failed", e.getCause());
            }
            for (Map.Entry<PairKey, List<OverlapResult>> e : part.entrySet()) {
                List<OverlapResult> overlaps = new ArrayList<>(e.getValue());
                if (overlaps.isEmpty()) {
                    continue;
                }
                long projectDays = 0L;
                for (OverlapResult o : overlaps) {
                    projectDays += o.days();
                }
                if (projectDays <= 0) {
                    continue;
                }
                candidates.add(new PairAggregate(e.getKey(), overlaps, projectDays));
                if (projectDays > max) {
                    max = projectDays;
                }
            }
        }

        if (candidates.isEmpty() || max <= 0) {
            return List.of();
        }

        long maxFinal = max;
        List<PairAggregate> winners = new ArrayList<>();
        for (PairAggregate c : candidates) {
            if (c.totalDays() == maxFinal) {
                winners.add(c);
            }
        }
        winners.sort(Comparator.<PairAggregate>comparingInt(p -> p.pair().a())
                .thenComparingInt(p -> p.pair().b())
                .thenComparingInt(p -> p.breakdown().get(0).projectId()));
        return winners;
    }
}

