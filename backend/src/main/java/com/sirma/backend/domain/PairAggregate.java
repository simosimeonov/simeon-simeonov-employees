package com.sirma.backend.domain;

import java.util.List;

public record PairAggregate(PairKey pair, List<OverlapResult> breakdown, long totalDays) {
}
