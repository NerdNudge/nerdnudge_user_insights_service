package com.neurospark.nerdnudge.userinsights.dto;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.neurospark.nerdnudge.userinsights.utils.Commons;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
public class UserHomePageStatsEntity {
    private String accountType = "Freemium";
    private int totalQuizzes = 0;
    private double correctPercentage = 0.0;
    private int highestInADay = 0;
    private int highestCorrectInADay = 0;
    private int currentStreak = 0;
    private int highestStreak = 0;
    private int quizflexCountToday = 0;
    private int shotsCountToday = 0;
    private String quoteOfTheDay = "";
    private int numPeopleUsedNerdNudgeToday = 654;


    public UserHomePageStatsEntity(JsonObject userProfileDocument) {
        updateAccountType(userProfileDocument);
        updateSummaryCounts(userProfileDocument);
        updateDayStatsCounts(userProfileDocument);
        updateStreakCounts(userProfileDocument);
        updateCurrentDayCounts(userProfileDocument);

    }

    private void updateAccountType(JsonObject userProfileDocument) {
        accountType = userProfileDocument.has("accountType") ? userProfileDocument.get("accountType").getAsString() : accountType;
    }

    private void updateCurrentDayCounts(JsonObject userProfileDocument) {
        if(! userProfileDocument.has("dayQuota"))
            return;

        JsonObject dayQuotaObject = userProfileDocument.get("dayQuota").getAsJsonObject();
        String currentDay = Commons.getDaystamp();
        if(! dayQuotaObject.has(currentDay))
            return;

        JsonArray currentDayArray = dayQuotaObject.get(currentDay).getAsJsonArray();
        quizflexCountToday = currentDayArray.get(0).getAsInt();
        shotsCountToday = currentDayArray.get(1).getAsInt();
    }

    private void updateSummaryCounts(JsonObject userProfileDocument) {
        if(userProfileDocument.has("Summary")) {
            JsonObject summaryObject = userProfileDocument.get("Summary").getAsJsonObject();
            if(summaryObject.has("overallSummary")) {
                JsonObject overallSummaryObject = summaryObject.get("overallSummary").getAsJsonObject();
                if(overallSummaryObject.has("total")) {
                    JsonArray totalArray = overallSummaryObject.get("total").getAsJsonArray();
                    totalQuizzes = totalArray.get(0).getAsInt();

                    if (totalQuizzes == 0) {
                        return;
                    }

                    double percentage = ((double) totalArray.get(1).getAsInt() / totalQuizzes) * 100;
                    BigDecimal bd = new BigDecimal(percentage).setScale(2, RoundingMode.HALF_UP);
                    correctPercentage = bd.doubleValue();
                }
            }
        }
    }

    private void updateDayStatsCounts(JsonObject userProfileDocument) {
        if(userProfileDocument.has("dayStats")) {
            JsonObject dayStatsObject = userProfileDocument.get("dayStats").getAsJsonObject();
            if(dayStatsObject.has("highest")) {
                highestInADay = dayStatsObject.get("highest").getAsInt();
                highestCorrectInADay = dayStatsObject.get("highestCorrect").getAsInt();
            }
        }
    }

    private void updateStreakCounts(JsonObject userProfileDocument) {
        if(userProfileDocument.has("streak")) {
            JsonObject streakObject = userProfileDocument.get("streak").getAsJsonObject();
            if(streakObject.has("current")) {
                currentStreak = streakObject.get("current").getAsInt();
                highestStreak = streakObject.get("highest").getAsInt();
            }
        }
    }
}
