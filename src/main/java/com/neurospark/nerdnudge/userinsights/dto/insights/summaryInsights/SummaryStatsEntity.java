package com.neurospark.nerdnudge.userinsights.dto.insights.summaryInsights;

import lombok.Data;

@Data
public class SummaryStatsEntity {
    private int rank;
    private StatsEntity stats;
    private AccuracyEntity accuracy;
}
