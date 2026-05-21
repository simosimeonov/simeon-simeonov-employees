package com.sirma.backend.api.dto;

import java.time.LocalDate;

public record PairProjectBreakdownDto(int projectId, LocalDate dateFrom, LocalDate dateTo, long days) {
}
