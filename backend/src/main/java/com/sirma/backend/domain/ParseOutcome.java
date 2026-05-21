package com.sirma.backend.domain;

import java.time.LocalDate;

public sealed interface ParseOutcome permits ParseOutcome.Valid, ParseOutcome.Invalid {

    record Valid(int projectId, int userId, LocalDate dateFrom, LocalDate dateTo) implements ParseOutcome {
    }

    record Invalid(long lineNumber, String rawLine, String reason) implements ParseOutcome {
    }
}
