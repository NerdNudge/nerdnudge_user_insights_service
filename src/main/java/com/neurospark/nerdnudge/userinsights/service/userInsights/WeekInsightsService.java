package com.neurospark.nerdnudge.userinsights.service.userInsights;

import com.google.gson.JsonObject;
import com.neurospark.nerdnudge.userinsights.dto.insights.common.MetricCards;
import com.neurospark.nerdnudge.userinsights.dto.insights.week.Last7WeeksProgress;
import com.neurospark.nerdnudge.userinsights.dto.insights.week.WeekInsights;
import com.neurospark.nerdnudge.userinsights.dto.insights.week.WeekProgress;
import com.neurospark.nerdnudge.userinsights.utils.Commons;

public class WeekInsightsService {
    public WeekInsights getWeekInsights(JsonObject userData) {
        WeekInsights weekInsights = new WeekInsights();
        weekInsights.setWeekProgress(getWeekProgress(userData));
        weekInsights.setMetricCards(getWeekMetricCards(userData));
        weekInsights.setLast7WeeksProgress(getLast7WeekProgress(userData));

        return weekInsights;
    }

    private WeekProgress getWeekProgress(JsonObject userData) {
        WeekProgress weekProgress = new WeekProgress();
        weekProgress.setThisWeekProgress(InsightsUtils.getLastXDayProgress(userData, 7));
        return weekProgress;
    }

    private MetricCards getWeekMetricCards(JsonObject userData) {
        MetricCards metricCards = new MetricCards();
        metricCards.setStreak(InsightsUtils.getStreak(userData));

        int totalQuiz = 0;
        int totalCorrect = 0;
        if (userData.has("Summary")) {
            JsonObject summaryObject = userData.get("Summary").getAsJsonObject();
            if (summaryObject.has("last30Days")) {
                JsonObject last30DaysObject = summaryObject.get("last30Days").getAsJsonObject();
                for (int i = 0; i < 7; i++) {
                    String dayToFetch = Commons.getDaystampBeforeXDays(i);
                    if (last30DaysObject.has(dayToFetch)) {
                        JsonObject thisDayObject = last30DaysObject.get(dayToFetch).getAsJsonObject();
                        totalQuiz += thisDayObject.get("total").getAsInt();
                        totalCorrect += thisDayObject.get("totalCorrect").getAsInt();
                    }
                }
            }
        }
        metricCards.setQuizzes(totalQuiz);
        metricCards.setAccuracy(Commons.getPercentage(totalQuiz, totalCorrect));
        metricCards.setShots(InsightsUtils.getShotsCounts(userData, 7));

        return metricCards;
    }

    private Last7WeeksProgress getLast7WeekProgress(JsonObject userData) {
        Last7WeeksProgress last7WeeksProgress = new Last7WeeksProgress();
        last7WeeksProgress.setLast7WeekProgress(InsightsUtils.getLastXWeekProgress(userData, 7));

        return last7WeeksProgress;
    }
}
