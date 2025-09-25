package com.neurospark.nerdnudge.userinsights.dto.insights.day;

import com.neurospark.nerdnudge.userinsights.dto.insights.common.MetricCards;
import lombok.Data;

@Data
public class DayInsights {
    private DayProgress dayProgress;
    private MetricCards metricCards;
    private Last7DayProgress last7DayProgress;
}
