package com.neurospark.nerdnudge.userinsights.dto.insights.summaryInsights;

import lombok.Data;

import java.util.List;

@Data
public class Last30DaysEntity {
    private SummaryStatsEntity summary;
    private List<String> userTopics;
}
