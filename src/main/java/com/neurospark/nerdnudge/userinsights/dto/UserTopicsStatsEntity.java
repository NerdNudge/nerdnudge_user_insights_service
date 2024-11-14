package com.neurospark.nerdnudge.userinsights.dto;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserTopicsStatsEntity {
    private double personalScoreIndicator;
    private String lastTaken;
    private JsonObject rwc;
}
