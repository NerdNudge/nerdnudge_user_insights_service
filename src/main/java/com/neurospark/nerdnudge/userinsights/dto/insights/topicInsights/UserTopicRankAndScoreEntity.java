package com.neurospark.nerdnudge.userinsights.dto.insights.topicInsights;

import lombok.Data;

import java.util.Map;

@Data
public class UserTopicRankAndScoreEntity {
    private String userId;
    private Map<String, Integer> topicsRank;
    private Map<String, Double> topicsScore;
}
