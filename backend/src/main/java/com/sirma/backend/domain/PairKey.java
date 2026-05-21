package com.sirma.backend.domain;

public record PairKey(int a, int b) {
    public PairKey {
        if (a == b) {
            throw new IllegalArgumentException("PairKey cannot have a == b (" + a + ")");
        }
        if (a > b) {
            throw new IllegalArgumentException("PairKey requires a < b — use PairKey.of(...)");
        }
    }

    public static PairKey of(int x, int y) {
        if (x == y) {
            throw new IllegalArgumentException("PairKey cannot have equal members: " + x);
        }
        return new PairKey(Math.min(x, y), Math.max(x, y));
    }
}
