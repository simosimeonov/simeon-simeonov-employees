package com.sirma.backend.service.date;

import com.sirma.backend.config.AppProperties;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Thread-safe memoization for date parsing. Caches both successful and
 * unparsable results, so repeated lookups for the same raw
 * string skip the parser entirely. When capacity is reached, the whole cache
 * is cleared.
 */
@Component
public class DateCache {

    private final ConcurrentHashMap<String, Optional<LocalDate>> cache = new ConcurrentHashMap<>();
    private final int maxSize;

    public DateCache(AppProperties props) {
        this.maxSize = props.dateCache().maxSize();
    }

    public Optional<LocalDate> get(String raw, Function<String, Optional<LocalDate>> loader) {
        Optional<LocalDate> hit = cache.get(raw);
        if (hit != null) {
            return hit;
        }
        if (cache.size() >= maxSize) {
            cache.clear();
        }
        return cache.computeIfAbsent(raw, loader);
    }

    public int size() {
        return cache.size();
    }

    public void clear() {
        cache.clear();
    }
}
