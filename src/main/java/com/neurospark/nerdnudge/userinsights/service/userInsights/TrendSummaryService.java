package com.neurospark.nerdnudge.userinsights.service.userInsights;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.neurospark.nerdnudge.couchbase.service.NerdPersistClient;
import com.neurospark.nerdnudge.userinsights.dto.insights.trendInsights.TrendSummaryEntity;

import java.util.Iterator;
import java.util.Map;

public class TrendSummaryService {
    public TrendSummaryEntity getTrendSummaryEntity(String userId, NerdPersistClient userProfilesPersist) {
        TrendSummaryEntity trendSummaryEntity = new TrendSummaryEntity();
        JsonObject userTrendsData = userProfilesPersist.get(userId + "-trends");
        if(userTrendsData == null || ! userTrendsData.has("trends"))
            return trendSummaryEntity;

        JsonObject trendsObject = userTrendsData.get("trends").getAsJsonObject();
        Iterator<Map.Entry<String, JsonElement>> userTrendsDataIterator = trendsObject.entrySet().iterator();
        while(userTrendsDataIterator.hasNext()) {
            Map.Entry<String, JsonElement> thisEntry = userTrendsDataIterator.next();
            String topic = thisEntry.getKey();
            trendSummaryEntity.getUserTrends().put(topic, thisEntry.getValue().getAsJsonObject());
        }

        return trendSummaryEntity;
    }
}
