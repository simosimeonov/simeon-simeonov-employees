package com.sirma.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app")
public record AppProperties(Csv csv, DateCacheCfg dateCache, Calculation calculation) {

    public record Csv(boolean hasHeader, String delimiter, int maxSizeMb) {}

    public record DateCacheCfg(int maxSize) {}

    public record Calculation(boolean inclusiveEndDate, int parallelism) {}
}
