package com.neurospark.nerdnudge.userinsights.dto;

import lombok.Data;

@Data
public class UserRecentFavoritesEntity {
    String id;
    String title;
    String topic_name;
    String sub_topic;
    String difficulty_level;
}
