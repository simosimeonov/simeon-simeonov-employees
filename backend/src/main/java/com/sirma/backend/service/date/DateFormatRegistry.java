package com.sirma.backend.service.date;

import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Component
public class DateFormatRegistry {

    private final List<DateTimeFormatter> formatters = List.of(
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("dd.MM.yyyy"),
            DateTimeFormatter.ofPattern("MM/dd/yyyy"),
            DateTimeFormatter.ofPattern("yyyy.MM.dd"),
            DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH),
            DateTimeFormatter.ISO_LOCAL_DATE
    );

    public List<DateTimeFormatter> formatters() {
        return formatters;
    }
}