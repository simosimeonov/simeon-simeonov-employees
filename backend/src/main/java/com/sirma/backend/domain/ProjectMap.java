package com.sirma.backend.domain;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class ProjectMap {
    private final ConcurrentHashMap<Integer, UserMap> byProject = new ConcurrentHashMap<>();

    public void add(int projectId, int userId, Period period) {
        byProject.computeIfAbsent(projectId, k -> new UserMap()).add(userId, period);
    }

    public Set<Integer> projectIds() {
        return byProject.keySet();
    }

    public UserMap get(int projectId) {
        return byProject.get(projectId);
    }

    public int size() {
        return byProject.size();
    }
}
