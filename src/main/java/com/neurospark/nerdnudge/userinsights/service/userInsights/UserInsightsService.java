package com.neurospark.nerdnudge.userinsights.service.userInsights;

import com.google.gson.JsonObject;

public interface UserInsightsService {
    public JsonObject getUserInsights(String userId);
}
