package com.sirma.backend.service.date;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Indexes date formatters by their shape signature at startup, so parse-time
 * lookups return one or two candidates instead of trying all ten. The
 * signature collapses digit runs to D, letter runs to A, and keeps
 * separators as-is (e.g. "2014-01-05" becomes "D4-D2-D2", "5 Jan 2014"
 * becomes "D1 A D4").
 */
@Component
public class DateFormatTree {

    private final Map<String, List<DateTimeFormatter>> bySignature;
    private final List<DateTimeFormatter> allFormatters;

    public DateFormatTree(DateFormatRegistry registry) {
        this.allFormatters = registry.formatters();
        this.bySignature = buildIndex(allFormatters);
    }

    public List<DateTimeFormatter> candidatesFor(String input) {
        if (input == null || input.isEmpty()) {
            return allFormatters;
        }
        List<DateTimeFormatter> match = bySignature.get(signature(input));
        return match != null ? match : allFormatters;
    }

    static Map<String, List<DateTimeFormatter>> buildIndex(List<DateTimeFormatter> formatters) {
        List<LocalDate> samples = List.of(
                LocalDate.of(2014, 1, 5),
                LocalDate.of(2014, 12, 25),
                LocalDate.of(2014, 9, 9),
                LocalDate.of(2014, 9, 30)
        );
        Map<String, List<DateTimeFormatter>> result = new LinkedHashMap<>();
        for (DateTimeFormatter fmt : formatters) {
            for (LocalDate sample : samples) {
                try {
                    String formatted = sample.format(fmt);
                    String sig = signature(formatted);
                    List<DateTimeFormatter> bucket =
                            result.computeIfAbsent(sig, k -> new ArrayList<>());
                    if (!bucket.contains(fmt)) {
                        bucket.add(fmt);
                    }
                } catch (RuntimeException ignored) {
                }
            }
        }
        Map<String, List<DateTimeFormatter>> frozen = new HashMap<>(result.size());
        result.forEach((k, v) -> frozen.put(k, List.copyOf(v)));
        return Map.copyOf(frozen);
    }

    static String signature(String s) {
        if (s == null || s.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder(s.length() + 4);
        int i = 0;
        int n = s.length();
        while (i < n) {
            char c = s.charAt(i);
            if (Character.isDigit(c)) {
                int count = 0;
                while (i < n && Character.isDigit(s.charAt(i))) {
                    count++;
                    i++;
                }
                sb.append('D').append(count);
            } else if (Character.isLetter(c)) {
                while (i < n && Character.isLetter(s.charAt(i))) {
                    i++;
                }
                sb.append('A');
            } else {
                sb.append(c);
                i++;
            }
        }
        return sb.toString();
    }
}

