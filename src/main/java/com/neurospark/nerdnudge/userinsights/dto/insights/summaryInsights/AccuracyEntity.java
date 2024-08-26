package com.neurospark.nerdnudge.userinsights.dto.insights.summaryInsights;

import lombok.Data;

@Data
public class AccuracyEntity {
    private double easy;
    private double medium;
    private double hard;
    private double overall;
}
