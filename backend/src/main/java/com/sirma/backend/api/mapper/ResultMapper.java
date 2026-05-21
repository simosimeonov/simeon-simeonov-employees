package com.sirma.backend.api.mapper;

import com.sirma.backend.api.dto.PairProjectBreakdownDto;
import com.sirma.backend.api.dto.PairResultDto;
import com.sirma.backend.domain.OverlapResult;
import com.sirma.backend.domain.PairAggregate;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class ResultMapper {

    public List<PairResultDto> toDto(List<PairAggregate> aggregates) {
        return aggregates.stream()
                .map(this::toPairResult)
                .toList();
    }

    private PairResultDto toPairResult(PairAggregate aggregate) {
        List<PairProjectBreakdownDto> breakdown = aggregate.breakdown().stream()
                .sorted(Comparator.comparingInt(OverlapResult::projectId))
                .map(o -> new PairProjectBreakdownDto(o.projectId(), o.from(), o.to(), o.days()))
                .toList();
        return new PairResultDto(
                aggregate.pair().a(),
                aggregate.pair().b(),
                aggregate.totalDays(),
                breakdown);
    }
}

