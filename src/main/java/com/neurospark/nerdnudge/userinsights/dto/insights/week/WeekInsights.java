package com.neurospark.nerdnudge.userinsights.dto.insights.week;

import com.neurospark.nerdnudge.userinsights.dto.insights.common.MetricCards;
import lombok.Data;

@Data
public class WeekInsights {
    private WeekProgress weekProgress;
    private MetricCards metricCards;
    private Last7WeeksProgress last7WeeksProgress;
}
