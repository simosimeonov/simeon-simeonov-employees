package com.sirma.backend.api.dto;

import java.util.List;

public record UploadResponse(List<PairResultDto> topPairs, List<InvalidRowDto> invalidRows, StatsDto stats) {
}
