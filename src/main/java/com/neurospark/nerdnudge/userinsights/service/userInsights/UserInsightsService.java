package com.neurospark.nerdnudge.userinsights.service.userInsights;

import com.google.gson.JsonObject;
import com.neurospark.nerdnudge.userinsights.dto.UserTopicsStatsEntity;

import java.util.Map;

public interface UserInsightsService {
    public JsonObject getUserInsights(String userId);

    public Map<String, UserTopicsStatsEntity> getUserTopicStats(String userId);
}
