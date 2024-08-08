package com.neurospark.nerdnudge.userinsights.service;

import com.google.gson.JsonObject;

public interface UserInsightsService {
    public JsonObject getUserInsights(String userId);
}
