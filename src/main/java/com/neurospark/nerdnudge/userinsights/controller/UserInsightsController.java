package com.neurospark.nerdnudge.userinsights.controller;

import com.neurospark.nerdnudge.metrics.metrics.Metric;
import com.neurospark.nerdnudge.userinsights.dto.UserHomePageStatsEntity;
import com.neurospark.nerdnudge.userinsights.dto.UserTopicsStatsEntity;
import com.neurospark.nerdnudge.userinsights.dto.insights.UserInsightsEntity;
import com.neurospark.nerdnudge.userinsights.response.ApiResponse;
import com.neurospark.nerdnudge.userinsights.service.userHomePage.UserHomePageStatsService;
import com.neurospark.nerdnudge.userinsights.service.userInsights.UserInsightsService;
import com.neurospark.nerdnudge.userinsights.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
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
        log.info("Get User Home Page Stats: {}", userId);
        UserHomePageStatsEntity userHomePageStatsEntity = userHomePageStatsService.getUserHomePageStats(userId);
        long endTime = System.currentTimeMillis();
        new Metric.MetricBuilder().setName("userHomeStatsFetch").setUnit(Metric.Unit.MILLISECONDS).setValue((endTime - startTime)).build();
        return new ApiResponse<>(Constants.SUCCESS, "User insights fetched successfully", userHomePageStatsEntity, (endTime - startTime));
    }

    @GetMapping("/getuserinsights/{id}")
    public ApiResponse<UserInsightsEntity> getUserInsights(@PathVariable(value = "id") String userId) {
        long startTime = System.currentTimeMillis();
        log.info("Get User Insights: {}", userId);
        UserInsightsEntity userInsightsEntity = userInsightsService.getUserInsights(userId);
        long endTime = System.currentTimeMillis();
        new Metric.MetricBuilder().setName("userInsightsFetch").setUnit(Metric.Unit.MILLISECONDS).setValue((endTime - startTime)).build();
        return new ApiResponse<>(Constants.SUCCESS, "User insights fetched successfully", userInsightsEntity, (endTime - startTime));
    }

    @GetMapping("/getUserTopicStats/{id}")
    public ApiResponse<Map<String, UserTopicsStatsEntity>> getUserTopicStats(@PathVariable(value = "id") String userId) {
        long startTime = System.currentTimeMillis();
        log.info("Get User Topic Stats: {}", userId);
        Map<String, UserTopicsStatsEntity> userTopicsStatsEntity = userInsightsService.getUserTopicStats(userId);
        long endTime = System.currentTimeMillis();
        new Metric.MetricBuilder().setName("userTopicStatsFetch").setUnit(Metric.Unit.MILLISECONDS).setValue((endTime - startTime)).build();
        return new ApiResponse<>(Constants.SUCCESS, "User topics stats fetched successfully", userTopicsStatsEntity, (endTime - startTime));
    }

    @GetMapping("/getUserTopicScore/{id}/{topic}")
    public ApiResponse<Double> getUserTopicScore(@PathVariable(value = "id") String userId, @PathVariable(value = "topic") String topic) {
        long startTime = System.currentTimeMillis();
        log.info("Get User Topic Score for user: {}, topic: {}", userId, topic);
        Double userTopicScore = userInsightsService.getUserTopicScore(userId, topic);
        long endTime = System.currentTimeMillis();
        new Metric.MetricBuilder().setName("userTopicScoreFetch").setUnit(Metric.Unit.MILLISECONDS).setValue((endTime - startTime)).build();
        return new ApiResponse<>(Constants.SUCCESS, "User topics score fetched successfully", userTopicScore, (endTime - startTime));
    }

    @GetMapping("/health")
    public ApiResponse<String> healthCheck() {
        return new ApiResponse<>(Constants.SUCCESS, "Health Check Pass", Constants.SUCCESS, 0);
    }
}
