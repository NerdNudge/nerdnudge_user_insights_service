package com.neurospark.nerdnudge.userinsights.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserTopicsStatsEntity {
    private String lastTaken;
    private int level;
}
