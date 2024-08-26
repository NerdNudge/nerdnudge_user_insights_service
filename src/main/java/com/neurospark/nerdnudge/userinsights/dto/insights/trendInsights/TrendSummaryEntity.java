package com.neurospark.nerdnudge.userinsights.dto.insights.trendInsights;

import lombok.Data;

import java.util.Map;

@Data
public class TrendSummaryEntity {
    private Map<String, Double> scoreTrend;
    private Map<String, Integer> rankTrend;
}
