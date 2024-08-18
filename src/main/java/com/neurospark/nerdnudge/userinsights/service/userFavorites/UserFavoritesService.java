package com.neurospark.nerdnudge.userinsights.service.userFavorites;

import com.google.gson.JsonObject;

import java.util.List;

public interface UserFavoritesService {
    public List<JsonObject> getRecentFavorites(String userId);
}
