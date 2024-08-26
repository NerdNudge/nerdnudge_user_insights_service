package com.neurospark.nerdnudge.userinsights.dto.insights.topicInsights;

import lombok.Data;

@Data
public class SubtopicEntity {
    private String id;
    private int questionsAttempted;
    private double percentageCorrect;
}
