package com.sirma.backend.service.calculation;

import com.sirma.backend.domain.OverlapResult;
import com.sirma.backend.domain.PairKey;
import com.sirma.backend.domain.UserMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class ProjectPairsTask implements Callable<Map<PairKey, List<OverlapResult>>> {

    private final int projectId;
    private final UserMap userMap;
    private final OverlapCalculator calculator;

    public ProjectPairsTask(int projectId, UserMap userMap, OverlapCalculator calculator) {
        this.projectId = projectId;
        this.userMap = userMap;
        this.calculator = calculator;
    }

    @Override
    public Map<PairKey, List<OverlapResult>> call() {
        Map<PairKey, List<OverlapResult>> local = new HashMap<>();
        List<Integer> userIds = new ArrayList<>(userMap.userIds());
        userIds.sort(Integer::compareTo);
        for (int i = 0; i < userIds.size(); i++) {
            int userA = userIds.get(i);
            for (int j = i + 1; j < userIds.size(); j++) {
                int userB = userIds.get(j);
                PairKey key = PairKey.of(userA, userB);
                List<OverlapResult> overlaps = calculator.overlapAll(projectId,
                        userMap.periodsOf(userA), userMap.periodsOf(userB));
                if (!overlaps.isEmpty()) {
                    local.computeIfAbsent(key, k -> new ArrayList<>()).addAll(overlaps);
                }
            }
        }
        return local;
    }
}
