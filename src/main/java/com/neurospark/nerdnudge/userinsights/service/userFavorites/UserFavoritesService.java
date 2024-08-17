package com.neurospark.nerdnudge.userinsights.service.userFavorites;

import com.neurospark.nerdnudge.userinsights.dto.UserRecentFavoritesEntity;

import java.util.List;

public interface UserFavoritesService {
    public List<UserRecentFavoritesEntity> getRecentFavorites(String userId);
}
