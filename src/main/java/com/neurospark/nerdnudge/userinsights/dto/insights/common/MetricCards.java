package com.neurospark.nerdnudge.userinsights.dto.insights.common;

import lombok.Data;

@Data
public class MetricCards {
    private int streak;
    private int quizzes;
    private double accuracy;
    private int shots;
}
