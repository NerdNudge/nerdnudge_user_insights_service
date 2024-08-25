package com.neurospark.nerdnudge.userinsights.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class UserFavoriteTopicsEntity {
    String topicName;
    int favoritesCount;
    List<SubtopicsWithCounts> subtopics;

    @Data
    @AllArgsConstructor
    public static class SubtopicsWithCounts {
        String name;
        int count;
    }
}

