package com.neurospark.nerdnudge.userinsights.service.userInsights;

import com.google.gson.JsonObject;
import com.neurospark.nerdnudge.userinsights.dto.insights.common.MetricCards;
import com.neurospark.nerdnudge.userinsights.dto.insights.month.Last6MonthsProgress;
import com.neurospark.nerdnudge.userinsights.dto.insights.month.MonthInsights;
import com.neurospark.nerdnudge.userinsights.dto.insights.month.MonthProgress;
import com.neurospark.nerdnudge.userinsights.utils.Commons;


public class MonthInsightsService {
    public MonthInsights getMonthInsights(JsonObject userData) {
        MonthInsights monthInsights = new MonthInsights();
        monthInsights.setMonthProgress(getMonthProgress(userData));
        monthInsights.setMetricCards(getMonthMetricCards(userData));
        monthInsights.setLast6MonthsProgress(getLast6MonthsProgress(userData));
        return monthInsights;
    }

    private MonthProgress getMonthProgress(JsonObject userData) {
        MonthProgress monthProgress = new MonthProgress();
        monthProgress.setThisMonthProgress(InsightsUtils.getLastXDayProgress(userData, 30));
        return monthProgress;
    }

    private MetricCards getMonthMetricCards(JsonObject userData) {
        MetricCards metricCards = new MetricCards();
        metricCards.setStreak(InsightsUtils.getStreak(userData));

        int totalQuiz = 0;
        int totalCorrect = 0;
        if(userData.has("Summary")) {
            JsonObject summaryObject = userData.get("Summary").getAsJsonObject();
            if(summaryObject.has("last30Days")) {
                JsonObject last30DaysObject = summaryObject.get("last30Days").getAsJsonObject();
                for (int i = 0; i < 30; i++) {
                    String dayToFetch = Commons.getDaystampBeforeXDays(i);
                    if(last30DaysObject.has(dayToFetch)) {
                        JsonObject thisDayObject = last30DaysObject.get(dayToFetch).getAsJsonObject();
                        totalQuiz += thisDayObject.get("total").getAsInt();
                        totalCorrect += thisDayObject.get("totalCorrect").getAsInt();
                    }
                }
            }
        }
        metricCards.setQuizzes(totalQuiz);
        metricCards.setAccuracy(Commons.getPercentage(totalQuiz, totalCorrect));
        metricCards.setShots(InsightsUtils.getShotsCounts(userData, 30));

        return metricCards;
    }

    private Last6MonthsProgress getLast6MonthsProgress(JsonObject userData) {
        Last6MonthsProgress last7MonthsProgress = new Last6MonthsProgress();
        last7MonthsProgress.setLast6MonthsProgress(InsightsUtils.getLastXWeekProgress(userData, 12));

        return last7MonthsProgress;
    }
}
