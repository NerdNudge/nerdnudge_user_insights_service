package com.neurospark.nerdnudge.userinsights.service.userInsights;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.neurospark.nerdnudge.userinsights.utils.Commons;

import java.util.ArrayList;
import java.util.List;

public class InsightsUtils {
    public static List<Integer> getLastXDayProgress(JsonObject userData, int xDays) {
        List<Integer> lastXDayProgress = new ArrayList<>();

        if(! userData.has("Summary"))
            return lastXDayProgress;

        JsonObject summaryObject = userData.get("Summary").getAsJsonObject();
        if(! summaryObject.has("last30Days"))
            return lastXDayProgress;

        JsonObject last30DaysObject = summaryObject.get("last30Days").getAsJsonObject();
        for(int i = 0; i < xDays; i ++) {
            String dayToFetch = Commons.getDaystampBeforeXDays(i);
            int valueToAdd = last30DaysObject.has(dayToFetch) ? last30DaysObject.get(dayToFetch).getAsJsonObject().get("total").getAsInt() : 0;
            lastXDayProgress.add(valueToAdd);
        }

        return lastXDayProgress;
    }

    public static List<Integer> getLastXWeekProgress(JsonObject userData, int xWeeks) {
        List<Integer> lastXWeekProgress = new ArrayList<>();

        if(! userData.has("Summary"))
            return lastXWeekProgress;

        JsonObject summaryObject = userData.get("Summary").getAsJsonObject();
        if(! summaryObject.has("weekly"))
            return lastXWeekProgress;

        JsonObject weekObject = summaryObject.get("weekly").getAsJsonObject();
        for(int i = 0; i < xWeeks; i ++) {
            String weekToFetch = Commons.getWeekStampBeforeXWeeks(i);
            int valueToAdd = weekObject.has(weekToFetch) ? weekObject.get(weekToFetch).getAsJsonArray().get(0).getAsInt() : 0;
            lastXWeekProgress.add(valueToAdd);
        }

        return lastXWeekProgress;
    }

    public static int getStreak(JsonObject userData) {
        String today = Commons.getDaystamp();
        int streak = 0;
        if (userData.has("lastQuizDate")) {
            if(userData.get("lastQuizDate").getAsString().equals(today)) {
                streak = userData.get("quizStreak").getAsInt();
            }
            else {
                int daysDiff = Commons.getDaysDifferenceFromToday(userData.get("lastQuizDate").getAsString());
                streak = daysDiff == 1 ? userData.get("quizStreak").getAsInt() : 0;
            }
        }
        return streak;
    }

    public static int getShotsCounts(JsonObject userData, int xDays) {
        int shots = 0;
        if(userData.has("dayQuota")) {
            JsonObject dayQuotaObject = userData.get("dayQuota").getAsJsonObject();
            for (int i = 0; i < xDays; i++) {
                String dayToFetch = Commons.getDaystampBeforeXDays(i);
                if (dayQuotaObject.has(dayToFetch)) {
                    JsonArray thisDayObject = dayQuotaObject.get(dayToFetch).getAsJsonArray();
                    shots += thisDayObject.get(1).getAsInt();
                }
            }
        }
        return shots;
    }
}
