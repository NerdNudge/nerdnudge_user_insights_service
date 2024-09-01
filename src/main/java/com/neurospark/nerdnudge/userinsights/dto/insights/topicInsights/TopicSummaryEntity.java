package com.neurospark.nerdnudge.userinsights.dto.insights.topicInsights;

import com.neurospark.nerdnudge.userinsights.dto.insights.summaryInsights.PeerComparisonEntity;
import lombok.Data;

import java.util.Map;

@Data
public class TopicSummaryEntity {
    private Map<String, TopicEntity> lifetime;
    private Map<String, TopicEntity> last30Days;
}
