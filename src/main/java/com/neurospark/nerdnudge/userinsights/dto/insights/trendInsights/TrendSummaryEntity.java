package com.neurospark.nerdnudge.userinsights.dto.insights.trendInsights;

import com.google.gson.JsonObject;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class TrendSummaryEntity {
    private Map<String, JsonObject> userTrends = new HashMap<>();
}
