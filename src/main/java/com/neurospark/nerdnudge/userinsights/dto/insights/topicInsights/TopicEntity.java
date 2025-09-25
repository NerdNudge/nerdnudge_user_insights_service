package com.neurospark.nerdnudge.userinsights.dto.insights.topicInsights;

import lombok.Data;

import java.util.Map;

@Data
public class TopicEntity {
    private int easy;
    private int medium;
    private int hard;
    private Map<String, Double> subtopics;
}
