package com.sirma.backend.api.dto;

public record StatsDto(long totalRows, long validRows, long invalidRows, int projectsProcessed, long elapsedMs) {
}
