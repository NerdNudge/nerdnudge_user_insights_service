package com.neurospark.nerdnudge.userinsights.dto;

import lombok.Data;

@Data
public class UserFavoriteTopicsEntity {
    String topicName;
    int numFavoriteSubtopics;
    int favoritesCount;
}
