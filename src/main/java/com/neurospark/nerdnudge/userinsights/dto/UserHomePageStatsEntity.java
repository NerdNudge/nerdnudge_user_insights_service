package com.neurospark.nerdnudge.userinsights.dto;

import lombok.Data;

@Data
public class UserHomePageStatsEntity {
    private String accountType = "Freemium";
    private int totalQuizzes = 0;
    private double correctPercentage = 0.0;
    private int highestInADay = 0;
    private int highestCorrectInADay = 0;
    private int currentStreak = 0;
    private int highestStreak = 0;
    private int quizflexCountToday = 0;
    private int shotsCountToday = 0;
    private String quoteOfTheDay = "You have brains in your head. You have feet in your shoes. You can steer yourself any direction you choose.";
    private String quoteAuthor = "Dr. Seuss (author)";
    private String quoteId = "q14";
    private long numPeopleUsedNerdNudgeToday = 654;
    private int adsFrequencyQuizFlex = 7;
    private int adsFrequencyShots = 9;
    private int quizflexQuota = 12;
    private int shotsQuota = 15;
}
