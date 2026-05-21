package com.sirma.backend.domain;

import java.time.LocalDate;

public record OverlapResult(int projectId, LocalDate from, LocalDate to, long days) {
}
