package com.neurospark.nerdnudge.userinsights.service.userInsights;

import com.google.gson.JsonObject;
import com.neurospark.nerdnudge.userinsights.dto.insights.common.MetricCards;
import com.neurospark.nerdnudge.userinsights.dto.insights.day.DayInsights;
import com.neurospark.nerdnudge.userinsights.dto.insights.day.DayProgress;
import com.neurospark.nerdnudge.userinsights.dto.insights.day.Last7DayProgress;
import com.neurospark.nerdnudge.userinsights.utils.Commons;

public class DayInsightsService {

    private int totalQuiz;
    private int totalCorrect;

    public DayInsights getDayInsights(JsonObject userData) {
        DayInsights dayInsights = new DayInsights();
        dayInsights.setDayProgress(getDayProgress(userData));
        dayInsights.setMetricCards(getDayMetricCards(userData));
        dayInsights.setLast7DayProgress(getLast7DayProgress(userData));

        return dayInsights;
    }

    private DayProgress getDayProgress(JsonObject userData) {
        DayProgress dayProgress = new DayProgress();
        if(! userData.has("Summary"))
            return dayProgress;

        JsonObject summaryObject = userData.get("Summary").getAsJsonObject();
        if(! summaryObject.has("last30Days"))
            return dayProgress;

        JsonObject last30DaysObject = summaryObject.get("last30Days").getAsJsonObject();
        String today = Commons.getDaystamp();
        if(! last30DaysObject.has(today))
            return dayProgress;

        JsonObject todayObject = last30DaysObject.get(today).getAsJsonObject();
        this.totalQuiz = todayObject.get("total").getAsInt();
        this.totalCorrect = todayObject.get("totalCorrect").getAsInt();

        dayProgress.setTotalQuiz(this.totalQuiz);
        dayProgress.setTotalCorrect(this.totalCorrect);

        return dayProgress;
    }

    private MetricCards getDayMetricCards(JsonObject userData) {
        MetricCards metricCards = new MetricCards();
        metricCards.setStreak(InsightsUtils.getStreak(userData));
        metricCards.setQuizzes(this.totalQuiz);
        double accuracy = Commons.getPercentage(this.totalQuiz, totalCorrect);
        metricCards.setAccuracy(accuracy);

        String today = Commons.getDaystamp();
        if (userData.has("dayQuota")) {
            JsonObject dayStatsObject = userData.get("dayQuota").getAsJsonObject();
            if (dayStatsObject.has(today)) {
                metricCards.setShots(dayStatsObject.get(today).getAsJsonArray().get(1).getAsInt());
            }
        }
        
        return metricCards;
    }

    private Last7DayProgress getLast7DayProgress(JsonObject userData) {
        Last7DayProgress last7DayProgress = new Last7DayProgress();
        last7DayProgress.setLast7DayProgress(InsightsUtils.getLastXDayProgress(userData, 7));
        return last7DayProgress;
    }
}
