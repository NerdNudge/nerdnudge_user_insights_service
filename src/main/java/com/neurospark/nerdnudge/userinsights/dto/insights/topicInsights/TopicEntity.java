package com.neurospark.nerdnudge.userinsights.dto.insights.topicInsights;

import lombok.Data;

import java.util.List;

@Data
public class TopicEntity {
    private int easy;
    private int medium;
    private int hard;
    private List<SubtopicEntity> subtopicEntities;
}
