package com.neurospark.nerdnudge.userinsights.service.userInsights;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.neurospark.nerdnudge.couchbase.service.NerdPersistClient;
import com.neurospark.nerdnudge.userinsights.dto.UserTopicsStatsEntity;
import com.neurospark.nerdnudge.userinsights.dto.insights.UserInsightsEntity;
import com.neurospark.nerdnudge.userinsights.utils.Commons;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Slf4j
@Service
public class UserInsightsServiceImpl implements UserInsightsService {

    private NerdPersistClient configPersist;
    private NerdPersistClient userProfilesPersist;
    private NerdPersistClient shotsStatsPersist;
    public static JsonObject topicNameToTopicCodeMapping = null;
    public static JsonObject topicCodeToTopicNameMapping = null;

    @Autowired
    public void UserInsightsServiceImpl(@Qualifier("configPersist") NerdPersistClient configPersist,
                                        @Qualifier("userProfilesPersist") NerdPersistClient userProfilesPersist,
                                        @Qualifier("shotsStatsPersist") NerdPersistClient shotsStatsPersist) {
        this.configPersist = configPersist;
        this.userProfilesPersist = userProfilesPersist;
        this.shotsStatsPersist = shotsStatsPersist;
        if(topicNameToTopicCodeMapping == null)
            updateTopicCodeMaps();
    }

    private void updateTopicCodeMaps() {
        log.info("Updating topic code map.");
        JsonObject topicCodeToTopicNameMappingObject = configPersist.get("collection_topic_mapping");
        topicNameToTopicCodeMapping = new JsonObject();
        topicCodeToTopicNameMapping = new JsonObject();

        Iterator<Map.Entry<String, JsonElement>> topicsIterator = topicCodeToTopicNameMappingObject.entrySet().iterator();
        while(topicsIterator.hasNext()) {
            Map.Entry<String, JsonElement> thisEntry = topicsIterator.next();
            topicNameToTopicCodeMapping.addProperty(thisEntry.getValue().getAsString(), thisEntry.getKey());
            topicCodeToTopicNameMapping.addProperty(thisEntry.getKey(), thisEntry.getValue().getAsString());
        }

        log.info("Topic Name To Codes Mapping: {}", topicNameToTopicCodeMapping);
        log.info("Topic Code To Names Mapping: {}", topicCodeToTopicNameMapping);
    }

    @Override
    public UserInsightsEntity getUserInsights(String userId) {
        JsonObject userData = Commons.getUserProfileDocument(userId, userProfilesPersist);
        UserInsightsEntity userInsightsEntity = new UserInsightsEntity();
        userInsightsEntity.setDayInsights(new DayInsightsService().getDayInsights(userData));
        userInsightsEntity.setWeekInsights(new WeekInsightsService().getWeekInsights(userData));
        userInsightsEntity.setMonthInsights(new MonthInsightsService().getMonthInsights(userData));

        return userInsightsEntity;
    }

    @Override
    public Map<String, UserTopicsStatsEntity> getUserTopicStats(String userId) {
        JsonObject userData = Commons.getUserProfileDocument(userId, userProfilesPersist);
        Map<String, UserTopicsStatsEntity> userTopicsStats = new HashMap<>();
        if(! userData.has("topicwise"))
            return userTopicsStats;

        JsonObject topicwiseObject = userData.get("topicwise").getAsJsonObject();
        if(!topicwiseObject.has("overall"))
            return userTopicsStats;

        JsonObject overallObject = topicwiseObject.get("overall").getAsJsonObject();
        Iterator<Map.Entry<String, JsonElement>> topicsIterator = overallObject.entrySet().iterator();
        while(topicsIterator.hasNext()) {
            Map.Entry<String, JsonElement> thisEntry = topicsIterator.next();
            String topic = thisEntry.getKey();
            JsonObject topicObject = thisEntry.getValue().getAsJsonObject();
            if(! topicObject.has("correct"))
                continue;

            long topicLastTaken = topicObject.get("lastTaken").getAsLong();
            int level = topicObject.has("level") ? topicObject.get("level").getAsInt() : 1;

            userTopicsStats.put(topic, new UserTopicsStatsEntity(lastTakenByUser(topicLastTaken), level));
        }

        return userTopicsStats;
    }

    @Override
    public Map<String, String> getUserSubtopicLevels(String topic, String userId) {
        JsonObject userData = Commons.getUserProfileDocument(userId, userProfilesPersist);
        Map<String, String> userSubtopicLevels = new HashMap<>();
        if(! userData.has("topicwise"))
            return userSubtopicLevels;

        JsonObject topicwiseObject = userData.get("topicwise").getAsJsonObject();
        if(!topicwiseObject.has("overall"))
            return userSubtopicLevels;

        JsonObject overallObject = topicwiseObject.get("overall").getAsJsonObject();
        if(! overallObject.has(topic))
            return userSubtopicLevels;

        JsonObject thisTopicObject = overallObject.get(topic).getAsJsonObject();
        if(! thisTopicObject.has("subtopicLevels"))
            return userSubtopicLevels;

        JsonObject subtopicUserLevelsObject = thisTopicObject.get("subtopicLevels").getAsJsonObject();
        Iterator<Map.Entry<String, JsonElement>> userLevelsIterator = subtopicUserLevelsObject.entrySet().iterator();
        while(userLevelsIterator.hasNext()) {
            Map.Entry<String, JsonElement> thisSubtopicEntry = userLevelsIterator.next();
            userSubtopicLevels.put(thisSubtopicEntry.getKey(), thisSubtopicEntry.getValue().getAsString());
        }

        return userSubtopicLevels;
    }

    private String lastTakenByUser(long epoch) {
        long daysDifference = Commons.getDaysDifference(epoch);
        if(daysDifference == 0)
            return "Today";

        if(daysDifference < 30)
            return (daysDifference + " days ago");

        LocalDateTime dateTime = Instant.ofEpochMilli(epoch).atZone(ZoneId.systemDefault()).toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        return dateTime.format(formatter);
    }
}
