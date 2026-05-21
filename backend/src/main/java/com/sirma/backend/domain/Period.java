package com.sirma.backend.domain;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public record Period(LocalDate from, LocalDate to) {
    public Period {
        Objects.requireNonNull(from, "from");
        Objects.requireNonNull(to, "to");
        if (to.isBefore(from)) {
            throw new IllegalArgumentException("Period.to (" + to + ") is before from (" + from + ")");
        }
    }

    public long days() {
        return ChronoUnit.DAYS.between(from, to);
    }

    public long inclusiveDays() {
        return days() + 1;
    }
}
