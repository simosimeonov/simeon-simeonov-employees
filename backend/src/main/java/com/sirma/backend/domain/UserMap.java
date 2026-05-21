package com.sirma.backend.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class UserMap {
    private final ConcurrentHashMap<Integer, List<Period>> byUser = new ConcurrentHashMap<>();

    public void add(int userId, Period period) {
        byUser.computeIfAbsent(userId,
                k -> Collections.synchronizedList(new ArrayList<>())).add(period);
    }

    public Set<Integer> userIds() {
        return byUser.keySet();
    }

    public List<Period> periodsOf(int userId) {
        return byUser.getOrDefault(userId, List.of());
    }
}