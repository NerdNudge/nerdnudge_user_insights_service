package com.neurospark.nerdnudge.userinsights.service.userInsights;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.neurospark.nerdnudge.couchbase.service.NerdPersistClient;
import com.neurospark.nerdnudge.userinsights.dto.UserTopicsStatsEntity;
import com.neurospark.nerdnudge.userinsights.dto.insights.UserInsightsEntity;
import com.neurospark.nerdnudge.userinsights.utils.Commons;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Service
public class UserInsightsServiceImpl implements UserInsightsService {

    private NerdPersistClient configPersist;
    private NerdPersistClient userProfilesPersist;
    private UserRanksAndScoresService userRanksAndScoresService;
    public static JsonObject topicNameToTopicCodeMapping = null;
    public static JsonObject topicCodeToTopicNameMapping = null;

    @Autowired
    public void UserInsightsServiceImpl(@Qualifier("configPersist") NerdPersistClient configPersist,
                                        @Qualifier("userProfilesPersist") NerdPersistClient userProfilesPersist,
                                        UserRanksAndScoresService userRanksAndScoresService) {
        this.configPersist = configPersist;
        this.userProfilesPersist = userProfilesPersist;
        this.userRanksAndScoresService = userRanksAndScoresService;
        if(topicNameToTopicCodeMapping == null)
            updateTopicCodeMaps();
    }

    private void updateTopicCodeMaps() {
        System.out.println("Updating topic code map.");
        JsonObject topicCodeToTopicNameMappingObject = configPersist.get("collection_topic_mapping");
        topicNameToTopicCodeMapping = new JsonObject();
        topicCodeToTopicNameMapping = new JsonObject();

        Iterator<Map.Entry<String, JsonElement>> topicsIterator = topicCodeToTopicNameMappingObject.entrySet().iterator();
        while(topicsIterator.hasNext()) {
            Map.Entry<String, JsonElement> thisEntry = topicsIterator.next();
            topicNameToTopicCodeMapping.addProperty(thisEntry.getValue().getAsString(), thisEntry.getKey());
            topicCodeToTopicNameMapping.addProperty(thisEntry.getKey(), thisEntry.getValue().getAsString());
        }

        System.out.println("Topic Name To Codes Mapping: " + topicNameToTopicCodeMapping);
        System.out.println("Topic Code To Names Mapping: " + topicCodeToTopicNameMapping);
    }

    @Override
    public UserInsightsEntity getUserInsights(String userId) {
        JsonObject userData = Commons.getUserProfileDocument(userId, userProfilesPersist);
        UserInsightsEntity userInsightsEntity = new UserInsightsEntity();
        userInsightsEntity.setOverallSummary(new OverallSummaryService().getOverallSummaryEntity(userData));
        userInsightsEntity.setTopicSummary(new TopicSummaryService().getTopicSummaryEntity(userData));
        userInsightsEntity.setTrendSummary(new TrendSummaryService().getTrendSummaryEntity(userId, userProfilesPersist));
        userInsightsEntity.setHeatMap(new HeatmapSummaryService().getHeatmapEntity(userData));
        userRanksAndScoresService.updateUserRanksAndScores(userInsightsEntity);

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
            JsonObject subtopicObject = thisEntry.getValue().getAsJsonObject();
            if(! subtopicObject.has("correct"))
                continue;

            JsonArray correctArray = subtopicObject.get("correct").getAsJsonArray();
            double userTopicScore = getUserTopicScoreIndicator(correctArray.get(0).getAsInt(), correctArray.get(1).getAsInt());
            long topicLastTaken = subtopicObject.get("lastTaken").getAsLong();
            userTopicsStats.put(topic, new UserTopicsStatsEntity(userTopicScore, lastTakenByUser(topicLastTaken)));
        }

        return userTopicsStats;
    }

    private double getUserTopicScoreIndicator(int numQuizflexes, int correct) {
        int maxQuestions = 2400;
        if (numQuizflexes == 0) {
            return 0.0;
        }

        double baseScore = ((double) correct / numQuizflexes) * 100;

        double weight = Math.log1p(numQuizflexes) / Math.log1p(maxQuestions);
        weight = Math.min(1.0, weight);
        double finalScore = baseScore * weight;
        return Math.round(finalScore * 100.0) / 100.0;
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
