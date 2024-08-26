package com.neurospark.nerdnudge.userinsights.service.userFavorites;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.neurospark.nerdnudge.couchbase.service.NerdPersistClient;
import com.neurospark.nerdnudge.userinsights.dto.QuotesEntity;
import com.neurospark.nerdnudge.userinsights.dto.UserFavoriteTopicsEntity;
import com.neurospark.nerdnudge.userinsights.response.ApiResponse;
import com.neurospark.nerdnudge.userinsights.service.quotes.QuotesService;
import com.neurospark.nerdnudge.userinsights.utils.Commons;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class UserFavoritesServiceImpl implements UserFavoritesService {
    @Value("${content.manager.api.baseurl:http://localhost:9093/api/nerdnudge/quizflexes}")
    private String contentManagerBaseUrl;

    private String recentFavoritesUrl = "/getFavoriteQuizflexesByIds";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private QuotesService quotesService;

    public NerdPersistClient userProfilesPersist;

    @Autowired
    public void UserFavoritesServiceImpl(@Qualifier("userProfilesPersist") NerdPersistClient userProfilesPersist) {
        this.userProfilesPersist = userProfilesPersist;
    }

    @Override
    public List<JsonObject> getRecentFavorites(String userId) {
        JsonObject userData = Commons.getUserProfileDocument(userId, userProfilesPersist);
        List<JsonObject> result = new ArrayList<>();
        if(! userData.has("favorites"))
            return result;

        JsonObject favoritesObject = userData.get("favorites").getAsJsonObject();
        if(! favoritesObject.has("recent"))
            return result;

        JsonArray recentArray = favoritesObject.get("recent").getAsJsonArray();
        System.out.println(contentManagerBaseUrl + recentFavoritesUrl);
        ApiResponse<List<JsonObject>> response = restTemplate.postForObject(contentManagerBaseUrl + recentFavoritesUrl, recentArray.toString(), ApiResponse.class);

        return response.getData();
    }

    @Override
    public List<UserFavoriteTopicsEntity> getFavoritesTopics(String userId) {
        JsonObject userData = Commons.getUserProfileDocument(userId, userProfilesPersist);
        List<UserFavoriteTopicsEntity> result = new ArrayList<>();
        if(! userData.has("favorites"))
            return result;

        JsonObject favoritesObject = userData.get("favorites").getAsJsonObject();
        if(!favoritesObject.has("topicwise"))
            return result;

        JsonObject topicwiseObject = favoritesObject.get("topicwise").getAsJsonObject();
        Iterator<Map.Entry<String, JsonElement>> topicsIterator = topicwiseObject.entrySet().iterator();
        while(topicsIterator.hasNext()) {
            Map.Entry<String, JsonElement> thisEntry = topicsIterator.next();
            JsonObject thisTopicObject = thisEntry.getValue().getAsJsonObject();

            UserFavoriteTopicsEntity userFavoriteTopicsEntity = new UserFavoriteTopicsEntity();
            userFavoriteTopicsEntity.setTopicName(thisEntry.getKey());
            userFavoriteTopicsEntity.setSubtopics(new ArrayList<>());

            Iterator<Map.Entry<String, JsonElement>> subtopicsIterator = thisTopicObject.entrySet().iterator();
            int totalFavorites = 0;
            while(subtopicsIterator.hasNext()) {
                Map.Entry<String, JsonElement> thisSubtopicEntry = subtopicsIterator.next();
                JsonArray thisSubtopicArray = thisSubtopicEntry.getValue().getAsJsonArray();
                totalFavorites += thisSubtopicArray.size();
                userFavoriteTopicsEntity.getSubtopics().add(new UserFavoriteTopicsEntity.SubtopicsWithCounts(thisSubtopicEntry.getKey(), thisSubtopicArray.size()));
            }

            userFavoriteTopicsEntity.setFavoritesCount(totalFavorites);
            result.add(userFavoriteTopicsEntity);
        }
        return result;
    }

    @Override
    public List<QuotesEntity> getFavoritesQuotes(String userId) {
        JsonObject userData = Commons.getUserProfileDocument(userId, userProfilesPersist);
        List<QuotesEntity> result = new ArrayList<>();
        if(! userData.has("favorites"))
            return result;

        JsonObject favoritesObject = userData.get("favorites").getAsJsonObject();
        if(!favoritesObject.has("quotes"))
            return result;

        JsonArray quotesArray = favoritesObject.get("quotes").getAsJsonArray();
        for(int i = 0; i < quotesArray.size(); i ++) {
            String thisQuoteId = quotesArray.get(i).getAsString();
            QuotesEntity thisQuoteEntity = quotesService.getQuoteById(thisQuoteId);
            if(thisQuoteEntity != null)
                result.add(thisQuoteEntity);
        }
        return result;
    }
}
