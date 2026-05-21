package com.sirma.backend.api.dto;

import java.util.List;

public record PairResultDto(int employee1, int employee2, long totalDays, List<PairProjectBreakdownDto> breakdown) {
}
