package com.neurospark.nerdnudge.userinsights.service.userFavorites;

import com.google.gson.JsonObject;
import com.neurospark.nerdnudge.userinsights.dto.UserFavoriteTopicsEntity;

import java.util.List;

public interface UserFavoritesService {
    public List<JsonObject> getRecentFavorites(String userId);

    public List<UserFavoriteTopicsEntity> getFavoritesTopics(String userId);
}
