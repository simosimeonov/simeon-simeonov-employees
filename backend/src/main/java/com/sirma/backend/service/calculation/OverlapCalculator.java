package com.sirma.backend.service.calculation;

import com.sirma.backend.config.AppProperties;
import com.sirma.backend.domain.OverlapResult;
import com.sirma.backend.domain.Period;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class OverlapCalculator {

    private final boolean inclusive;

    public OverlapCalculator(AppProperties props) {
        this.inclusive = props.calculation().inclusiveEndDate();
    }

    public Optional<OverlapResult> overlap(int projectId, Period a, Period b) {
        LocalDate from = a.from().isAfter(b.from()) ? a.from() : b.from();
        LocalDate to = a.to().isBefore(b.to()) ? a.to() : b.to();
        if (to.isBefore(from)) {
            return Optional.empty();
        }
        long base = ChronoUnit.DAYS.between(from, to);
        long days = inclusive ? base + 1 : base;
        if (days <= 0) {
            return Optional.empty();
        }
        return Optional.of(new OverlapResult(projectId, from, to, days));
    }

    public List<OverlapResult> overlapAll(int projectId, List<Period> as, List<Period> bs) {
        List<OverlapResult> out = new ArrayList<>();
        for (Period a : as) {
            for (Period b : bs) {
                overlap(projectId, a, b).ifPresent(out::add);
            }
        }
        return out;
    }
}
