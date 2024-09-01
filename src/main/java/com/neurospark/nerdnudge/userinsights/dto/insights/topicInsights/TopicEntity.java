package com.neurospark.nerdnudge.userinsights.dto.insights.topicInsights;

import com.neurospark.nerdnudge.userinsights.dto.insights.summaryInsights.PeerComparisonEntity;
import lombok.Data;

import java.util.Map;

@Data
public class TopicEntity {
    private int easy;
    private int medium;
    private int hard;
    private Map<String, Double> subtopics;
    private PeerComparisonEntity peerComparison;
}
