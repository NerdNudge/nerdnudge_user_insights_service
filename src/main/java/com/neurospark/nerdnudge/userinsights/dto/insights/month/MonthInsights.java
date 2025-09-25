package com.neurospark.nerdnudge.userinsights.dto.insights.month;

import com.neurospark.nerdnudge.userinsights.dto.insights.common.MetricCards;
import lombok.Data;

@Data
public class MonthInsights {
    private MonthProgress monthProgress;
    private MetricCards metricCards;
    private Last6MonthsProgress last6MonthsProgress;
}
