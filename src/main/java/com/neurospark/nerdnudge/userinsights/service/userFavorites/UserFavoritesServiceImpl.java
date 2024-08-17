package com.neurospark.nerdnudge.userinsights.service.userFavorites;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.neurospark.nerdnudge.couchbase.service.NerdPersistClient;
import com.neurospark.nerdnudge.userinsights.dto.UserRecentFavoritesEntity;
import com.neurospark.nerdnudge.userinsights.response.ApiResponse;
import com.neurospark.nerdnudge.userinsights.utils.Commons;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class UserFavoritesServiceImpl implements UserFavoritesService {
    @Value("${content.manager.api.baseurl:http://localhost:9093/api/nerdnudge/quizflexes}")
    private String contentManagerBaseUrl;

    private String recentFavoritesUrl = "/getFavoriteQuizflexesByIds";

    @Autowired
    private RestTemplate restTemplate;

    public NerdPersistClient userProfilesPersist;

    @Autowired
    public void UserFavoritesServiceImpl(@Qualifier("userProfilesPersist") NerdPersistClient userProfilesPersist) {
        this.userProfilesPersist = userProfilesPersist;
    }

    @Override
    public List<UserRecentFavoritesEntity> getRecentFavorites(String userId) {
        JsonObject userData = Commons.getUserProfileDocument(userId, userProfilesPersist);
        List<UserRecentFavoritesEntity> result = new ArrayList<>();
        if(! userData.has("favorites"))
            return result;

        JsonObject favoritesObject = userData.get("favorites").getAsJsonObject();
        if(! favoritesObject.has("recent"))
            return result;

        JsonArray recentArray = favoritesObject.get("recent").getAsJsonArray();
        System.out.println(contentManagerBaseUrl + recentFavoritesUrl);
        ApiResponse<List<UserRecentFavoritesEntity>> response = restTemplate.postForObject(contentManagerBaseUrl + recentFavoritesUrl, recentArray.toString(), ApiResponse.class);

        return response.getData();
    }
}
