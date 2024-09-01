package com.neurospark.nerdnudge.userinsights.service.userInsights;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.neurospark.nerdnudge.couchbase.service.NerdPersistClient;
import com.neurospark.nerdnudge.userinsights.dto.insights.summaryInsights.PeerComparisonEntity;
import com.neurospark.nerdnudge.userinsights.utils.Commons;

import java.util.ArrayList;
import java.util.List;

public class PeerComparisonService {
    public PeerComparisonEntity getPeerComparisonEntity(String topicCode, JsonObject userData, NerdPersistClient shotsStatsPersist) {
        PeerComparisonEntity peerComparisonEntity = new PeerComparisonEntity();

        double userAverage = 0.0;
        JsonObject overallSummaryObject = getInputJsonObject(topicCode, userData);
        if(topicCode.equals("global")) {
            if(overallSummaryObject.has("total")) {
                JsonArray totalArray = overallSummaryObject.get("total").getAsJsonArray();
                userAverage = Commons.getPercentage(totalArray.get(0).getAsInt(), totalArray.get(1).getAsInt());
            }
        } else {
            userAverage = getUserAverageForTopic(overallSummaryObject);
        }

        peerComparisonEntity.setUserAverage(userAverage);

        long topicAttempts = shotsStatsPersist.getCounter(topicCode + "_attempts");
        long topicCorrect = shotsStatsPersist.getCounter(topicCode + "_correct");
        peerComparisonEntity.setPeersAverage(Commons.getPercentage((int) topicAttempts, (int) topicCorrect));

        peerComparisonEntity.setEasy(getDifficultyStats(topicCode, shotsStatsPersist, "Easy", overallSummaryObject));
        peerComparisonEntity.setMedium(getDifficultyStats(topicCode, shotsStatsPersist, "Medium", overallSummaryObject));
        peerComparisonEntity.setHard(getDifficultyStats(topicCode, shotsStatsPersist, "Hard", overallSummaryObject));

        return peerComparisonEntity;
    }

    private double getUserAverageForTopic(JsonObject inputObject) {
        int total = 0;
        int correct = 0;
        JsonArray easyArray = getUserDifficultyCountsForTopic(inputObject, "easy");
        JsonArray medArray = getUserDifficultyCountsForTopic(inputObject, "medium");
        JsonArray hardArray = getUserDifficultyCountsForTopic(inputObject, "hard");

        total += easyArray.get(0).getAsInt();
        total += medArray.get(0).getAsInt();
        total += hardArray.get(0).getAsInt();

        correct += easyArray.get(1).getAsInt();
        correct += medArray.get(1).getAsInt();
        correct += hardArray.get(1).getAsInt();

        return Commons.getPercentage(total, correct);
    }

    private JsonArray getUserDifficultyCountsForTopic(JsonObject inputObject, String difficulty) {
        if(inputObject.has(difficulty)) {
            return inputObject.get(difficulty).getAsJsonArray();
        }
        else {
            JsonArray dummy = new JsonArray();
            dummy.add(0);
            dummy.add(0);
            return dummy;
        }
    }

    private JsonObject getInputJsonObject(String topicCode, JsonObject data) {
        if(topicCode.equals("global")) {
            JsonObject overallSummaryObject = null;
            if(data.has("Summary")) {
                JsonObject summaryObject = data.get("Summary").getAsJsonObject();
                if(summaryObject.has("overallSummary")) {
                    overallSummaryObject = summaryObject.get("overallSummary").getAsJsonObject();
                    return overallSummaryObject;
                }
            }
        }
        else {
            return data;
        }
        return null;
    }

    private List<Double> getDifficultyStats(String topicCode, NerdPersistClient shotsStatsPersist, String difficulty, JsonObject overallSummaryObject) {
        long attempts = shotsStatsPersist.getCounter(topicCode + "_" + difficulty + "_attempts");
        long correct = shotsStatsPersist.getCounter(topicCode + "_" + difficulty + "_correct");

        List<Double> arrayList = new ArrayList<>();
        arrayList.add(getUsersDifficultyPercentage(overallSummaryObject, difficulty.toLowerCase()));
        arrayList.add(Commons.getPercentage((int) attempts, (int) correct));
        return arrayList;
    }

    private double getUsersDifficultyPercentage(JsonObject overallSummaryObject, String difficulty) {
        if(overallSummaryObject == null || ! overallSummaryObject.has(difficulty))
            return 0.0;

        JsonArray difficultyArray = overallSummaryObject.get(difficulty).getAsJsonArray();
        return Commons.getPercentage(difficultyArray.get(0).getAsInt(), difficultyArray.get(1).getAsInt());
    }
}
