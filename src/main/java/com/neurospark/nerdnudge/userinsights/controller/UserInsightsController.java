package com.neurospark.nerdnudge.userinsights.controller;

import com.google.gson.JsonObject;
import com.neurospark.nerdnudge.userinsights.dto.UserHomePageStatsEntity;
import com.neurospark.nerdnudge.userinsights.response.ApiResponse;
import com.neurospark.nerdnudge.userinsights.service.UserHomePageStatsService;
import com.neurospark.nerdnudge.userinsights.service.UserInsightsService;
import com.neurospark.nerdnudge.userinsights.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/nerdnudge/userinsights")
public class UserInsightsController {

    @Autowired
    UserInsightsService userInsightsService;

    @Autowired
    UserHomePageStatsService userHomePageStatsService;

    @GetMapping("/getUserHomePageStats/{id}")
    public ApiResponse<UserHomePageStatsEntity> getUserHomePageStats(@PathVariable(value = "id") String userId) {
        long startTime = System.currentTimeMillis();
        System.out.println("User Home Stats in request: " + userId);
        UserHomePageStatsEntity userHomePageStatsEntity = userHomePageStatsService.getUserHomePageStats(userId);
        long endTime = System.currentTimeMillis();
        return new ApiResponse<>(Constants.SUCCESS, "User insights fetched successfully", userHomePageStatsEntity, (endTime - startTime));
    }

    @GetMapping("/getuserinsights/{id}")
    public ApiResponse<JsonObject> getUserInsights(@PathVariable(value = "id") String userId) {
        long startTime = System.currentTimeMillis();
        System.out.println("User Insights Data in request: " + userId);
        JsonObject userInsightsEntity = userInsightsService.getUserInsights(userId);
        long endTime = System.currentTimeMillis();
        return new ApiResponse<>(Constants.SUCCESS, "User insights fetched successfully", userInsightsEntity, (endTime - startTime));
    }
}
