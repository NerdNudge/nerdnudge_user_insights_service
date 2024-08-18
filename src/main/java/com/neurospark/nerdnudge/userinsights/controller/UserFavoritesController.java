package com.neurospark.nerdnudge.userinsights.controller;

import com.google.gson.JsonObject;
import com.neurospark.nerdnudge.userinsights.dto.UserRecentFavoritesEntity;
import com.neurospark.nerdnudge.userinsights.response.ApiResponse;
import com.neurospark.nerdnudge.userinsights.service.userFavorites.UserFavoritesService;
import com.neurospark.nerdnudge.userinsights.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/nerdnudge/favorites")
public class UserFavoritesController {

    @Autowired
    UserFavoritesService userFavoritesService;

    @GetMapping("/getUserRecentFavorites/{id}")
    public ApiResponse<List<JsonObject>> getUserRecentFavorites(@PathVariable(value = "id") String userId) {
        long startTime = System.currentTimeMillis();
        System.out.println("User Insights Data in request: " + userId);
        List<JsonObject> userRecentFavoritesEntities = userFavoritesService.getRecentFavorites(userId);
        long endTime = System.currentTimeMillis();
        return new ApiResponse<>(Constants.SUCCESS, "User recent favorites fetched successfully", userRecentFavoritesEntities, (endTime - startTime));
    }

    @GetMapping("/getUserFavoriteTopics/{id}")
    public ApiResponse<List<JsonObject>> getUserFavoriteTopics(@PathVariable(value = "id") String userId) {
        long startTime = System.currentTimeMillis();
        System.out.println("User Insights Data in request: " + userId);


        long endTime = System.currentTimeMillis();
        return new ApiResponse<>(Constants.SUCCESS, "User recent favorites fetched successfully", userRecentFavoritesEntities, (endTime - startTime));
    }
}
