package com.neurospark.nerdnudge.userinsights.service.userInsights;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.neurospark.nerdnudge.couchbase.service.NerdPersistClient;
import com.neurospark.nerdnudge.userinsights.dto.insights.UserInsightsEntity;
import com.neurospark.nerdnudge.userinsights.dto.insights.trendInsights.TrendSummaryEntity;
import com.neurospark.nerdnudge.userinsights.utils.Commons;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TrendSummaryService {
    public TrendSummaryEntity getTrendSummaryEntity(String userId, NerdPersistClient userProfilesPersist, UserInsightsEntity userInsightsEntity) {
        TrendSummaryEntity trendSummaryEntity = new TrendSummaryEntity();
        JsonObject userTrendsData = userProfilesPersist.get(userId + "-trends");
        if(userTrendsData == null || ! userTrendsData.has("trends")) {
            getFirstTimeDataFromRanksAndScores(userInsightsEntity, trendSummaryEntity);
            return trendSummaryEntity;
        }

        Map<String, Integer> userRankings = userInsightsEntity.getRankings();
        Map<String, Double> userScores = userInsightsEntity.getScores();
        String currentDay = Commons.getDaystamp();

        JsonObject trendsObject = userTrendsData.get("trends").getAsJsonObject();
        Iterator<Map.Entry<String, JsonElement>> userTrendsDataIterator = trendsObject.entrySet().iterator();
        while(userTrendsDataIterator.hasNext()) {
            Map.Entry<String, JsonElement> thisEntry = userTrendsDataIterator.next();
            String topic = thisEntry.getKey();
            JsonObject topicTrends = thisEntry.getValue().getAsJsonObject();
            JsonArray currentDayArray = topicTrends.has(currentDay) ? topicTrends.get(currentDay).getAsJsonArray() : new JsonArray();
            topicTrends.add(currentDay, currentDayArray);
            if(currentDayArray.size() > 0) {
                if (userScores.containsKey(topic))
                    currentDayArray.set(0, new JsonPrimitive(userScores.get(topic)));

                if (userRankings.containsKey(topic))
                    currentDayArray.set(1, new JsonPrimitive(userRankings.get(topic)));
            } else {
                if (userScores.containsKey(topic))
                    currentDayArray.add(new JsonPrimitive(userScores.get(topic)));

                if (userRankings.containsKey(topic))
                    currentDayArray.add(new JsonPrimitive(userRankings.get(topic)));
            }

            trendSummaryEntity.getUserTrends().put(topic, thisEntry.getValue().getAsJsonObject());
        }

        return trendSummaryEntity;
    }

    private void getFirstTimeDataFromRanksAndScores(UserInsightsEntity userInsightsEntity, TrendSummaryEntity trendSummaryEntity) {
        Map<String, Integer> userRankings = userInsightsEntity.getRankings();
        Map<String, Double> userScores = userInsightsEntity.getScores();
        String currentDay = Commons.getDaystamp();

        for(String topic: userScores.keySet()) {
            JsonArray currentDayArray = new JsonArray();
            double score = userScores.get(topic);
            int rank = (userRankings.containsKey(topic)) ? userRankings.get(topic) : 0;

            currentDayArray.add(new JsonPrimitive(score));
            currentDayArray.add(new JsonPrimitive(rank));

            Map<String, JsonObject>  userTrends = trendSummaryEntity.getUserTrends();
            if(userTrends == null)
                userTrends = new HashMap<>();

            JsonObject topicObject = new JsonObject();
            topicObject.add(currentDay, currentDayArray);

            userTrends.put(topic, topicObject);
            trendSummaryEntity.setUserTrends(userTrends);
        }
    }
}
