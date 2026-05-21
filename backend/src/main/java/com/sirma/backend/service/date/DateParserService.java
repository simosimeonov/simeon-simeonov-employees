package com.sirma.backend.service.date;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

/**
 * Parses raw date strings into LocalDate.
 * Delegates to DateCache and DateFormatTree.
 */
@Service
public class DateParserService {

    private final DateFormatTree tree;
    private final DateCache cache;

    public DateParserService(DateFormatTree tree, DateCache cache) {
        this.tree = tree;
        this.cache = cache;
    }

    public boolean isNullSentinel(String raw) {
        if (raw == null) {
            return true;
        }
        String trimmed = raw.trim();
        return trimmed.isEmpty() || trimmed.equalsIgnoreCase("NULL");
    }

    public Optional<LocalDate> parse(String raw) {
        if (isNullSentinel(raw)) {
            return Optional.empty();
        }
        String trimmed = raw.trim();
        return cache.get(trimmed, this::doParse);
    }

    private Optional<LocalDate> doParse(String trimmed) {
        for (DateTimeFormatter fmt : tree.candidatesFor(trimmed)) {
            try {
                return Optional.of(LocalDate.parse(trimmed, fmt));
            } catch (DateTimeParseException ignored) {
            }
        }
        return Optional.empty();
    }
}

