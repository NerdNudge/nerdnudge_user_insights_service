package com.neurospark.nerdnudge.userinsights.controller;

import com.google.gson.JsonObject;
import com.neurospark.nerdnudge.metrics.metrics.Metric;
import com.neurospark.nerdnudge.userinsights.dto.QuotesEntity;
import com.neurospark.nerdnudge.userinsights.dto.UserFavoriteTopicsEntity;
import com.neurospark.nerdnudge.userinsights.response.ApiResponse;
import com.neurospark.nerdnudge.userinsights.service.userFavorites.UserFavoritesService;
import com.neurospark.nerdnudge.userinsights.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/nerdnudge/favorites")
public class UserFavoritesController {

    @Autowired
    UserFavoritesService userFavoritesService;

    @GetMapping("/getUserRecentFavorites/{id}")
    public ApiResponse<List<JsonObject>> getUserRecentFavorites(@PathVariable(value = "id") String userId) {
        long startTime = System.currentTimeMillis();
        log.info("Get User Recent Favorites: {}", userId);
        List<JsonObject> userRecentFavoritesEntities = userFavoritesService.getRecentFavorites(userId);
        long endTime = System.currentTimeMillis();
        new Metric.MetricBuilder().setName("recentFavsFetch").setUnit(Metric.Unit.MILLISECONDS).setValue((endTime - startTime)).build();
        return new ApiResponse<>(Constants.SUCCESS, "User recent favorites fetched successfully", userRecentFavoritesEntities, (endTime - startTime));
    }

    @GetMapping("/getUserFavoriteTopics/{id}")
    public ApiResponse<List<UserFavoriteTopicsEntity>> getUserFavoriteTopics(@PathVariable(value = "id") String userId) {
        long startTime = System.currentTimeMillis();
        log.info("Get User Favorite Topics: {}", userId);
        List<UserFavoriteTopicsEntity> result = userFavoritesService.getFavoritesTopics(userId);
        long endTime = System.currentTimeMillis();
        new Metric.MetricBuilder().setName("userFavTopicsFetch").setUnit(Metric.Unit.MILLISECONDS).setValue((endTime - startTime)).build();
        return new ApiResponse<>(Constants.SUCCESS, "User favorite topics fetched successfully", result, (endTime - startTime));
    }

    @GetMapping("/getUserFavoriteSubtopics/{topic}/{subtopic}/{id}")
    public ApiResponse<List<JsonObject>> getUserFavoriteSubtopics(@PathVariable(value = "topic") String topic, @PathVariable(value = "subtopic") String subtopic, @PathVariable(value = "id") String userId) {
        long startTime = System.currentTimeMillis();
        log.info("Get User Favorites for sub-topic: {}, topic: {}, user: {}", subtopic, topic, userId);
        List<JsonObject> result = userFavoritesService.getUserFavoriteSubtopics(topic, subtopic, userId);
        long endTime = System.currentTimeMillis();
        new Metric.MetricBuilder().setName("userFavSubtopicFetch").setUnit(Metric.Unit.MILLISECONDS).setValue((endTime - startTime)).build();
        return new ApiResponse<>(Constants.SUCCESS, "User favorite subtopics fetched successfully", result, (endTime - startTime));
    }

    @GetMapping("/getUserFavoriteQuotes/{id}")
    public ApiResponse<List<QuotesEntity>> getUserFavoriteQuotes(@PathVariable(value = "id") String userId) {
        long startTime = System.currentTimeMillis();
        log.info("Get User Favorite Quotes: {}", userId);
        List<QuotesEntity> result = userFavoritesService.getFavoritesQuotes(userId);
        long endTime = System.currentTimeMillis();
        new Metric.MetricBuilder().setName("userFavQuotesFetch").setUnit(Metric.Unit.MILLISECONDS).setValue((endTime - startTime)).build();
        return new ApiResponse<>(Constants.SUCCESS, "User favorite quotes fetched successfully", result, (endTime - startTime));
    }
}
