package com.sirma.backend.api.dto;

public record InvalidRowDto(long lineNumber, String rawLine, String reason) {
}
