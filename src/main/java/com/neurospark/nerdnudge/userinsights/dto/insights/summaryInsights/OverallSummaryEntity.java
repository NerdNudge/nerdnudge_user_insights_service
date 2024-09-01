package com.neurospark.nerdnudge.userinsights.dto.insights.summaryInsights;

import lombok.Data;

@Data
public class OverallSummaryEntity {
    private LifetimeEntity lifetime;
    private Last30DaysEntity last30Days;
    private PeerComparisonEntity peerComparison;
}
