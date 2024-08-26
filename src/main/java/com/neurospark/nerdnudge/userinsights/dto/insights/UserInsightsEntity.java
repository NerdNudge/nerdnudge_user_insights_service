package com.neurospark.nerdnudge.userinsights.dto.insights;

import com.neurospark.nerdnudge.userinsights.dto.insights.summaryInsights.OverallSummaryEntity;
import com.neurospark.nerdnudge.userinsights.dto.insights.topicInsights.TopicSummaryEntity;
import com.neurospark.nerdnudge.userinsights.dto.insights.trendInsights.TrendSummaryEntity;
import lombok.Data;

import java.util.Map;

@Data
public class UserInsightsEntity {
    private OverallSummaryEntity overallSummary;
    private TopicSummaryEntity topicSummary;
    private TrendSummaryEntity trendSummary;
    private Map<String, Integer[]> heatMap;
}
