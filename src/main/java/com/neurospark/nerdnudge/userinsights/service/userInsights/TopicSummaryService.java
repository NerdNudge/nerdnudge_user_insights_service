package com.neurospark.nerdnudge.userinsights.service.userInsights;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.neurospark.nerdnudge.couchbase.service.NerdPersistClient;
import com.neurospark.nerdnudge.userinsights.dto.insights.summaryInsights.PeerComparisonEntity;
import com.neurospark.nerdnudge.userinsights.dto.insights.topicInsights.TopicEntity;
import com.neurospark.nerdnudge.userinsights.dto.insights.topicInsights.TopicSummaryEntity;
import com.neurospark.nerdnudge.userinsights.utils.Commons;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TopicSummaryService {
    public TopicSummaryEntity getTopicSummaryEntity(JsonObject userData, NerdPersistClient shotsStatsPersist) {
        TopicSummaryEntity topicSummaryEntity = new TopicSummaryEntity();
        if(! userData.has("topicwise"))
            return topicSummaryEntity;

        topicSummaryEntity.setLifetime(getLifetimeTopicSummary(userData.get("topicwise").getAsJsonObject(), shotsStatsPersist));
        topicSummaryEntity.setLast30Days(getLast30DaysTopicSummary(userData.get("topicwise").getAsJsonObject(), topicSummaryEntity.getLifetime()));

        return topicSummaryEntity;
    }

    private Map<String, TopicEntity> getLifetimeTopicSummary(JsonObject topicwiseObject, NerdPersistClient shotsStatsPersist) {
        Map<String, TopicEntity> lifetimeSummary = new HashMap<>();
        if(! topicwiseObject.has("overall"))
            return lifetimeSummary;

        JsonObject overallObject = topicwiseObject.get("overall").getAsJsonObject();
        Iterator<Map.Entry<String, JsonElement>> topicsIterator = overallObject.entrySet().iterator();
        while(topicsIterator.hasNext()) {
            Map.Entry<String, JsonElement> thisEntry = topicsIterator.next();
            String topicId = thisEntry.getKey();
            if(! UserInsightsServiceImpl.topicCodeToTopicNameMapping.has(topicId))
                continue;

            JsonObject topicObject = thisEntry.getValue().getAsJsonObject();

            TopicEntity thisTopicEntity = new TopicEntity();
            if(topicObject.has("summary")) {
                JsonArray summaryArray = topicObject.get("summary").getAsJsonArray();
                thisTopicEntity.setEasy(summaryArray.get(0).getAsInt());
                thisTopicEntity.setMedium(summaryArray.get(1).getAsInt());
                thisTopicEntity.setHard(summaryArray.get(2).getAsInt());
            }

            thisTopicEntity.setSubtopics(new HashMap<>());
            if(topicObject.has("subtopics")) {
                JsonObject subtopicsObject = topicObject.get("subtopics").getAsJsonObject();
                Iterator<Map.Entry<String, JsonElement>> subtopicsIterator = subtopicsObject.entrySet().iterator();
                while(subtopicsIterator.hasNext()) {
                    Map.Entry<String, JsonElement> thisSubtopicEntry = subtopicsIterator.next();
                    String subtopicId = thisSubtopicEntry.getKey();
                    JsonArray subtopicArray = thisSubtopicEntry.getValue().getAsJsonArray();
                    double percentCorrect = Commons.getPercentage(subtopicArray.get(0).getAsInt(), subtopicArray.get(1).getAsInt());
                    thisTopicEntity.getSubtopics().put(subtopicId, percentCorrect);
                }
            }
            thisTopicEntity.setPeerComparison(new PeerComparisonService().getPeerComparisonEntity(topicId, topicObject, shotsStatsPersist));
            lifetimeSummary.put(topicId, thisTopicEntity);
        }

        return lifetimeSummary;
    }

    //TODO: Temporarily added peercomparison of lifetime into last 30 days.
    private Map<String, TopicEntity> getLast30DaysTopicSummary(JsonObject topicwiseObject, Map<String, TopicEntity> lifetimeEntity) {
        Map<String, TopicEntity> last30DaysSummary = new HashMap<>();
        if(! topicwiseObject.has("last30Days"))
            return last30DaysSummary;

        JsonObject last30DaysObject = topicwiseObject.get("last30Days").getAsJsonObject();
        Iterator<Map.Entry<String, JsonElement>> last30DaysIterator = last30DaysObject.entrySet().iterator();
        while (last30DaysIterator.hasNext()) {
            Map.Entry<String, JsonElement> currentTopicEntry = last30DaysIterator.next();
            String currentTopic = currentTopicEntry.getKey();
            if(! UserInsightsServiceImpl.topicCodeToTopicNameMapping.has(currentTopic))
                continue;

            JsonObject currentTopicObject = currentTopicEntry.getValue().getAsJsonObject();
            if(! currentTopicObject.has("summary"))
                continue;

            JsonObject summaryObject = currentTopicObject.get("summary").getAsJsonObject();
            TopicEntity thisTopicEntity = new TopicEntity();
            JsonArray last30DaysSubtopicCounts = getLast30DaysSubtopicDifficultyCounts(summaryObject);
            if(last30DaysSubtopicCounts == null)
                continue;

            thisTopicEntity.setEasy(last30DaysSubtopicCounts.get(0).getAsInt());
            thisTopicEntity.setMedium(last30DaysSubtopicCounts.get(1).getAsInt());
            thisTopicEntity.setHard(last30DaysSubtopicCounts.get(2).getAsInt());

            thisTopicEntity.setSubtopics(getSubtopicsForLast30Days(currentTopicObject));

            thisTopicEntity.setPeerComparison(lifetimeEntity.get(currentTopic).getPeerComparison());
            last30DaysSummary.put(currentTopic, thisTopicEntity);
        }
        return last30DaysSummary;
    }

    private Map<String, Double> getSubtopicsForLast30Days(JsonObject currentTopicObject) {
        Map<String, Double> subtopicsMap = new HashMap<>();
        if(!currentTopicObject.has("subtopics"))
            return subtopicsMap;

        Iterator<Map.Entry<String, JsonElement>> subtopicsIterator = currentTopicObject.get("subtopics").getAsJsonObject().entrySet().iterator();
        while(subtopicsIterator.hasNext()) {
            Map.Entry<String, JsonElement> thisEntry = subtopicsIterator.next();
            String subtopic = thisEntry.getKey();
            int currentTotal = 0;
            int currentCorrect = 0;
            JsonObject subtopicObject = thisEntry.getValue().getAsJsonObject();
            Iterator<Map.Entry<String, JsonElement>> subtopicDatesIterator = subtopicObject.entrySet().iterator();
            while(subtopicDatesIterator.hasNext()) {
                Map.Entry<String, JsonElement> subtopicEntry = subtopicDatesIterator.next();
                String currentDate = subtopicEntry.getKey();
                if(Commons.getDaysDifferenceFromToday(currentDate) > 30)
                    continue;

                JsonArray currentDateArray = subtopicEntry.getValue().getAsJsonArray();
                currentTotal += currentDateArray.get(0).getAsInt();
                currentCorrect += currentDateArray.get(1).getAsInt();
            }

            if(currentTotal > 0) {
                subtopicsMap.put(subtopic, Commons.getPercentage(currentTotal, currentCorrect));
            }
        }
        return subtopicsMap;
    }

    private JsonArray getLast30DaysSubtopicDifficultyCounts(JsonObject summaryObject) {
        JsonArray result = null;
        Iterator<Map.Entry<String, JsonElement>> summaryIterator = summaryObject.entrySet().iterator();
        while (summaryIterator.hasNext()) {
            Map.Entry<String, JsonElement> thisEntry = summaryIterator.next();
            String currentEntryDay = thisEntry.getKey();
            int daysDifference = Commons.getDaysDifferenceFromToday(currentEntryDay);
            if(daysDifference >= 0 || daysDifference <= 30) {
                if(result == null) {
                    result = new JsonArray();
                    result.add(0);
                    result.add(0);
                    result.add(0);
                }

                JsonArray currentDayArray = thisEntry.getValue().getAsJsonArray();
                result.set(0, new JsonPrimitive(result.get(0).getAsInt() + currentDayArray.get(0).getAsInt()));
                result.set(1, new JsonPrimitive(result.get(1).getAsInt() + currentDayArray.get(1).getAsInt()));
                result.set(2, new JsonPrimitive(result.get(2).getAsInt() + currentDayArray.get(2).getAsInt()));
            }
        }
        return result;
    }
}
