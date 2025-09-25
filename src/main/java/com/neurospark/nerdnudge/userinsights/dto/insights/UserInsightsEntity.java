package com.neurospark.nerdnudge.userinsights.dto.insights;

import com.neurospark.nerdnudge.userinsights.dto.insights.day.DayInsights;
import com.neurospark.nerdnudge.userinsights.dto.insights.month.MonthInsights;
import com.neurospark.nerdnudge.userinsights.dto.insights.week.WeekInsights;
import lombok.Data;

@Data
public class UserInsightsEntity {
    private DayInsights dayInsights;
    private WeekInsights weekInsights;
    private MonthInsights monthInsights;
}
