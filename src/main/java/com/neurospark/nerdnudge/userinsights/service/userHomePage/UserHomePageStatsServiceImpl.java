package com.neurospark.nerdnudge.userinsights.service.userHomePage;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.neurospark.nerdnudge.couchbase.service.NerdPersistClient;
import com.neurospark.nerdnudge.userinsights.dto.QuotesEntity;
import com.neurospark.nerdnudge.userinsights.dto.UserHomePageStatsEntity;
import com.neurospark.nerdnudge.userinsights.service.quotes.QuotesService;
import com.neurospark.nerdnudge.userinsights.utils.Commons;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Service
public class UserHomePageStatsServiceImpl implements UserHomePageStatsService{

    private NerdPersistClient userProfilesPersist;
    private NerdPersistClient configPersist;
    private JsonObject nerdConfigDocument = null;

    @Autowired
    private QuotesService quotesService;

    @Autowired
    public void UserInsightsServiceImpl(@Qualifier("userProfilesPersist") NerdPersistClient userProfilesPersist,
                                        @Qualifier("configPersist") NerdPersistClient configPersist) {
        this.userProfilesPersist = userProfilesPersist;
        this.configPersist = configPersist;
    }

    @Override
    public UserHomePageStatsEntity getUserHomePageStats(String id) {
        log.info("Getting home page stats for user: {}", id);
        UserHomePageStatsEntity userHomePageStatsEntity = new UserHomePageStatsEntity();
        updateUserHomePageStatsEntity(Commons.getUserProfileDocument(id, userProfilesPersist), userHomePageStatsEntity);
        return userHomePageStatsEntity;
    }

    private void updateUserHomePageStatsEntity(JsonObject userProfileDocument, UserHomePageStatsEntity userHomePageStatsEntity) {
        updateAccountType(userProfileDocument, userHomePageStatsEntity);
        updateSummaryCounts(userProfileDocument, userHomePageStatsEntity);
        updateDayStatsCounts(userProfileDocument, userHomePageStatsEntity);
        updateStreakCounts(userProfileDocument, userHomePageStatsEntity);
        updateCurrentDayCounts(userProfileDocument, userHomePageStatsEntity);
        updateQuote(userHomePageStatsEntity);
        updateNumPeopleUsedNerdNudgeToday(userHomePageStatsEntity);
        updateAdsFrequency(userHomePageStatsEntity);
        updateNerdQuota(userHomePageStatsEntity);
        updateLast7DaysActivity(userProfileDocument, userHomePageStatsEntity);
    }

    private void updateLast7DaysActivity(JsonObject userProfileDocument, UserHomePageStatsEntity userHomePageStatsEntity) {
        JsonObject result = new JsonObject();
        userHomePageStatsEntity.setLast7DaysActivity(result);
        if(! userProfileDocument.has("Summary"))
            return;

        JsonObject summaryObject = userProfileDocument.get("Summary").getAsJsonObject();
        if(! summaryObject.has("last30Days"))
            return;

        JsonObject last30DaysObject = summaryObject.get("last30Days").getAsJsonObject();
        for(int i = 0; i < 8; i ++) {
            String dayStamp = Commons.getDaystampBeforeXDays(i);
            if(! last30DaysObject.has(dayStamp))
                continue;

            JsonObject thisDayObject = last30DaysObject.get(dayStamp).getAsJsonObject();
            JsonArray easyArray = thisDayObject.get("easy").getAsJsonArray();
            JsonArray medArray = thisDayObject.get("medium").getAsJsonArray();
            JsonArray hardArray = thisDayObject.get("hard").getAsJsonArray();

            int totalQuestions = 0;
            int totalCorrect = 0;
            totalQuestions += easyArray.get(0).getAsInt();
            totalQuestions += medArray.get(0).getAsInt();
            totalQuestions += hardArray.get(0).getAsInt();

            totalCorrect += easyArray.get(1).getAsInt();
            totalCorrect += medArray.get(1).getAsInt();
            totalCorrect += hardArray.get(1).getAsInt();

            result.addProperty(dayStamp, Commons.getPercentage(totalQuestions, totalCorrect));
        }
    }

    private void updateNerdQuota(UserHomePageStatsEntity userHomePageStatsEntity) {
        if(nerdConfigDocument == null)
            nerdConfigDocument = configPersist.get("nerd_config");

        JsonObject dailyNerdQuota = nerdConfigDocument.get("dailyNerdQuota").getAsJsonObject();
        JsonObject quizFlexQuota = dailyNerdQuota.get("quizflex").getAsJsonObject();
        JsonObject shotsQuota = dailyNerdQuota.get("shots").getAsJsonObject();

        userHomePageStatsEntity.setQuizflexQuota(quizFlexQuota.get(userHomePageStatsEntity.getAccountType()).getAsInt());
        userHomePageStatsEntity.setShotsQuota(shotsQuota.get(userHomePageStatsEntity.getAccountType()).getAsInt());
    }

    private void updateAdsFrequency(UserHomePageStatsEntity userHomePageStatsEntity) {
        if(nerdConfigDocument == null)
            nerdConfigDocument = configPersist.get("nerd_config");

        if(userHomePageStatsEntity.getAccountType().equalsIgnoreCase("freemium")) {
            userHomePageStatsEntity.setAdsFrequencyQuizFlex(nerdConfigDocument.get("freemiumAdsFrequency_quizflex").getAsInt());
            userHomePageStatsEntity.setAdsFrequencyShots(nerdConfigDocument.get("freemiumAdsFrequency_shots").getAsInt());
        } else {
            userHomePageStatsEntity.setAdsFrequencyQuizFlex(0);
            userHomePageStatsEntity.setAdsFrequencyShots(0);
        }
    }

    private void updateNumPeopleUsedNerdNudgeToday(UserHomePageStatsEntity userHomePageStatsEntity) {
        String currentDay = Commons.getDaystamp();
        userHomePageStatsEntity.setNumPeopleUsedNerdNudgeToday(userProfilesPersist.getCounter(currentDay + "_user_counts"));
    }

    private void updateQuote(UserHomePageStatsEntity userHomePageStatsEntity) {
        QuotesEntity quoteOfTheDay = quotesService.getQuoteOfTheDay();
        userHomePageStatsEntity.setQuoteId(quoteOfTheDay.getQuotesId());
        userHomePageStatsEntity.setQuoteOfTheDay(quoteOfTheDay.getQuote());

        StringBuilder quotesAuthorBuilder = new StringBuilder();
        quotesAuthorBuilder.append(quoteOfTheDay.getAuthor());

        if(! quoteOfTheDay.getAuthorCredentials().isEmpty()) {
            quotesAuthorBuilder.append(" (");
            quotesAuthorBuilder.append(quoteOfTheDay.getAuthorCredentials());
            quotesAuthorBuilder.append(")");
        }
        userHomePageStatsEntity.setQuoteAuthor(quotesAuthorBuilder.toString());
    }

    private void updateAccountType(JsonObject userProfileDocument, UserHomePageStatsEntity userHomePageStatsEntity) {
        if(userProfileDocument.has("accountType")) {
            userHomePageStatsEntity.setAccountType(userProfileDocument.get("accountType").getAsString());
        }
    }

    private void updateCurrentDayCounts(JsonObject userProfileDocument, UserHomePageStatsEntity userHomePageStatsEntity) {
        if(! userProfileDocument.has("dayQuota"))
            return;

        JsonObject dayQuotaObject = userProfileDocument.get("dayQuota").getAsJsonObject();
        String currentDay = Commons.getDaystamp();
        if(! dayQuotaObject.has(currentDay))
            return;

        JsonArray currentDayArray = dayQuotaObject.get(currentDay).getAsJsonArray();
        userHomePageStatsEntity.setQuizflexCountToday(currentDayArray.get(0).getAsInt());
        userHomePageStatsEntity.setShotsCountToday(currentDayArray.get(1).getAsInt());
    }

    private void updateSummaryCounts(JsonObject userProfileDocument, UserHomePageStatsEntity userHomePageStatsEntity) {
        if(userProfileDocument.has("Summary")) {
            JsonObject summaryObject = userProfileDocument.get("Summary").getAsJsonObject();
            if(summaryObject.has("overallSummary")) {
                JsonObject overallSummaryObject = summaryObject.get("overallSummary").getAsJsonObject();
                if(overallSummaryObject.has("total")) {
                    JsonArray totalArray = overallSummaryObject.get("total").getAsJsonArray();
                    userHomePageStatsEntity.setTotalQuizzes(totalArray.get(0).getAsInt());

                    if (userHomePageStatsEntity.getTotalQuizzes() == 0) {
                        return;
                    }

                    double percentage = ((double) totalArray.get(1).getAsInt() / userHomePageStatsEntity.getTotalQuizzes()) * 100;
                    BigDecimal bd = new BigDecimal(percentage).setScale(2, RoundingMode.HALF_UP);
                    userHomePageStatsEntity.setCorrectPercentage(bd.doubleValue());
                }
            }
        }
    }

    private void updateDayStatsCounts(JsonObject userProfileDocument, UserHomePageStatsEntity userHomePageStatsEntity) {
        if(userProfileDocument.has("dayStats")) {
            JsonObject dayStatsObject = userProfileDocument.get("dayStats").getAsJsonObject();
            if(dayStatsObject.has("highest")) {
                userHomePageStatsEntity.setHighestInADay(dayStatsObject.get("highest").getAsInt());
                userHomePageStatsEntity.setHighestCorrectInADay(dayStatsObject.get("highestCorrect").getAsInt());
            }
        }
    }

    private void updateStreakCounts(JsonObject userProfileDocument, UserHomePageStatsEntity userHomePageStatsEntity) {
        if(userProfileDocument.has("streak")) {
            JsonObject streakObject = userProfileDocument.get("streak").getAsJsonObject();
            if(streakObject.has("current")) {
                userHomePageStatsEntity.setCurrentStreak(streakObject.get("current").getAsInt());
                userHomePageStatsEntity.setHighestStreak(streakObject.get("highest").getAsInt());
            }
        }
    }
}
