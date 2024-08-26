package com.neurospark.nerdnudge.userinsights.service.userInsights;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.neurospark.nerdnudge.userinsights.dto.insights.summaryInsights.*;
import com.neurospark.nerdnudge.userinsights.utils.Commons;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class OverallSummaryService {
    public OverallSummaryEntity getOverallSummaryEntity(JsonObject userData) {
        OverallSummaryEntity overallSummaryEntity = new OverallSummaryEntity();
        overallSummaryEntity.setLifetime(getLifetimeEntity(userData));
        overallSummaryEntity.setLast30Days(getLast30DaysEntity(userData));
        return overallSummaryEntity;
    }

    private LifetimeEntity getLifetimeEntity(JsonObject userData) {
        LifetimeEntity lifetimeEntity = new LifetimeEntity();
        lifetimeEntity.setSummary(getLifetimeSummaryStatsEntity(userData));
        lifetimeEntity.setUserTopics(getLifetimeUserTopics(userData));
        return lifetimeEntity;
    }

    private List<String> getLifetimeUserTopics(JsonObject userData) {
        List<String> userTopics = new ArrayList<>();
        if(! userData.has("topicwise"))
            return userTopics;

        JsonObject topicwiseObject = userData.get("topicwise").getAsJsonObject();
        if(!topicwiseObject.has("overall"))
            return userTopics;

        JsonObject overallObject = topicwiseObject.get("overall").getAsJsonObject();
        Iterator<Map.Entry<String, JsonElement>> topicsIterator = overallObject.entrySet().iterator();

        while(topicsIterator.hasNext()) {
            userTopics.add(topicsIterator.next().getKey());
        }

        return userTopics;
    }

    private List<String> getLast30DaysUserTopics(JsonObject userData) {
        List<String> userTopics = new ArrayList<>();
        if(! userData.has("topicwise"))
            return userTopics;

        JsonObject topicwiseObject = userData.get("topicwise").getAsJsonObject();
        if(!topicwiseObject.has("last30Days"))
            return userTopics;

        JsonObject last30DaysObject = topicwiseObject.get("last30Days").getAsJsonObject();
        Iterator<Map.Entry<String, JsonElement>> topicsIterator = last30DaysObject.entrySet().iterator();

        while(topicsIterator.hasNext()) {
            Map.Entry<String, JsonElement> thisEntry = topicsIterator.next();
            String topic = thisEntry.getKey();
            JsonObject topicObject = thisEntry.getValue().getAsJsonObject();
            if(! topicObject.has("summary"))
                continue;

            JsonObject summaryObject = topicObject.get("summary").getAsJsonObject();
            Iterator<Map.Entry<String, JsonElement>> datesIterator = summaryObject.entrySet().iterator();
            while (datesIterator.hasNext()) {
                String currentDateKey = datesIterator.next().getKey();
                int dayDifference = Commons.getDaysDifferenceFromToday(currentDateKey);
                if(dayDifference >= 0 && dayDifference <= 30) {
                    userTopics.add(topic);
                    break;
                }
            }
        }
        return userTopics;
    }

    private SummaryStatsEntity getLifetimeSummaryStatsEntity(JsonObject userData) {
        SummaryStatsEntity summaryStatsEntity = new SummaryStatsEntity();
        JsonObject overallSummaryObject = getLifetimeOverallSummaryObject(userData);

        summaryStatsEntity.setStats(getStatsEntity(overallSummaryObject));
        summaryStatsEntity.setAccuracy(getAccuracyEntity(overallSummaryObject));
        return summaryStatsEntity;
    }

    private SummaryStatsEntity getLast30DaysSummaryStatsEntity(JsonObject userData) {
        SummaryStatsEntity summaryStatsEntity = new SummaryStatsEntity();
        JsonObject overallSummaryObject = getLast30DaysOverallSummaryObject(userData);
        System.out.println("Last 30 days overall summary object created: " + overallSummaryObject);

        summaryStatsEntity.setStats(getStatsEntity(overallSummaryObject));
        summaryStatsEntity.setAccuracy(getAccuracyEntity(overallSummaryObject));
        return summaryStatsEntity;
    }

    private JsonObject getLifetimeOverallSummaryObject(JsonObject userData) {
        if(! userData.has("Summary"))
            return null;

        JsonObject summaryObject = userData.get("Summary").getAsJsonObject();
        if(! summaryObject.has("overallSummary"))
            return null;

        return summaryObject.get("overallSummary").getAsJsonObject();
    }


    private JsonObject getLast30DaysOverallSummaryObject(JsonObject userData) {
        if(! userData.has("Summary"))
            return null;

        JsonObject summaryObject = userData.get("Summary").getAsJsonObject();
        if(! summaryObject.has("last30Days"))
            return null;

        JsonObject last30DaysObject = summaryObject.get("last30Days").getAsJsonObject();
        JsonObject overallObject = new JsonObject();
        overallObject.add("total", getArrayOf2Elements());
        overallObject.add("easy", getArrayOf2Elements());
        overallObject.add("medium", getArrayOf2Elements());
        overallObject.add("hard", getArrayOf2Elements());

        Iterator<Map.Entry<String, JsonElement>> last30DaysIterator = last30DaysObject.entrySet().iterator();
        while(last30DaysIterator.hasNext()) {
            Map.Entry<String, JsonElement> thisEntry = last30DaysIterator.next();
            String currentKey = thisEntry.getKey();

            int daysDifference = Commons.getDaysDifferenceFromToday(currentKey);
            if (daysDifference >= 0 && daysDifference < 30) {
                System.out.println("Success Current key: " + currentKey);
                JsonObject thisDayObject = thisEntry.getValue().getAsJsonObject();
                addToOverallObject(thisDayObject, overallObject, "easy");
                addToOverallObject(thisDayObject, overallObject, "medium");
                addToOverallObject(thisDayObject, overallObject, "hard");
            }
        }
        return overallObject;
    }

    private void addToOverallObject(JsonObject thisDayObject, JsonObject overallObject, String difficulty) {
        if(thisDayObject.has(difficulty)) {
            JsonArray totalArray = overallObject.get("total").getAsJsonArray();
            JsonArray difficultyArray = overallObject.get(difficulty).getAsJsonArray();
            JsonArray currentDifficultyArray = thisDayObject.get(difficulty).getAsJsonArray();

            totalArray.set(0, new JsonPrimitive(totalArray.get(0).getAsInt() + currentDifficultyArray.get(0).getAsInt()));
            difficultyArray.set(0, new JsonPrimitive(difficultyArray.get(0).getAsInt() + currentDifficultyArray.get(0).getAsInt()));

            totalArray.set(1, new JsonPrimitive(totalArray.get(1).getAsInt() + currentDifficultyArray.get(1).getAsInt()));
            difficultyArray.set(1, new JsonPrimitive(difficultyArray.get(1).getAsInt() + currentDifficultyArray.get(1).getAsInt()));
        }
    }

    private JsonArray getArrayOf2Elements() {
        JsonArray array = new JsonArray();
        array.add(0);
        array.add(0);

        return array;
    }

    private StatsEntity getStatsEntity(JsonObject overallSummaryObject) {
        StatsEntity statsEntity = new StatsEntity();
        if(overallSummaryObject == null)
            return statsEntity;

        if(overallSummaryObject.has("easy"))
            statsEntity.setEasy(overallSummaryObject.get("easy").getAsJsonArray().get(0).getAsInt());

        if(overallSummaryObject.has("medium"))
            statsEntity.setMedium(overallSummaryObject.get("medium").getAsJsonArray().get(0).getAsInt());

        if(overallSummaryObject.has("hard"))
            statsEntity.setHard(overallSummaryObject.get("hard").getAsJsonArray().get(0).getAsInt());

        return statsEntity;
    }

    private AccuracyEntity getAccuracyEntity(JsonObject overallSummaryObject) {
        AccuracyEntity accuracyEntity = new AccuracyEntity();
        if(overallSummaryObject == null)
            return accuracyEntity;

        accuracyEntity.setEasy(getPercentage(overallSummaryObject, "easy"));
        accuracyEntity.setMedium(getPercentage(overallSummaryObject, "medium"));
        accuracyEntity.setHard(getPercentage(overallSummaryObject, "hard"));
        accuracyEntity.setOverall(getPercentage(overallSummaryObject, "total"));

        return accuracyEntity;
    }

    private double getPercentage(JsonObject overallSummaryObject, String difficulty) {
        if(overallSummaryObject.has(difficulty)) {
            JsonArray array = overallSummaryObject.get(difficulty).getAsJsonArray();
            return getPercentage(array.get(0).getAsInt(), array.get(1).getAsInt());
        }
        return 0.0;
    }

    private double getPercentage(int total, int correct) {
        if(total == 0)
            return 0.0;

        double percentage = ((double) correct / total) * 100;
        return Math.round(percentage * 100.0) / 100.0;
    }

    private Last30DaysEntity getLast30DaysEntity(JsonObject userData) {
        Last30DaysEntity last30DaysEntity = new Last30DaysEntity();
        last30DaysEntity.setSummary(getLast30DaysSummaryStatsEntity(userData));
        last30DaysEntity.setUserTopics(getLast30DaysUserTopics(userData));
        return last30DaysEntity;
    }
}
