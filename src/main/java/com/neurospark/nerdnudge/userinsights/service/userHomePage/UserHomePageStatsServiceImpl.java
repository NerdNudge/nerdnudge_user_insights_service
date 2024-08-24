package com.neurospark.nerdnudge.userinsights.service.userHomePage;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.neurospark.nerdnudge.couchbase.service.NerdPersistClient;
import com.neurospark.nerdnudge.userinsights.dto.QuotesEntity;
import com.neurospark.nerdnudge.userinsights.dto.UserHomePageStatsEntity;
import com.neurospark.nerdnudge.userinsights.utils.Commons;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

@Service
public class UserHomePageStatsServiceImpl implements UserHomePageStatsService{

    private NerdPersistClient userProfilesPersist;
    private NerdPersistClient configPersist;
    private QuotesEntity quotesEntity;
    Random random = new Random();

    @Autowired
    public void UserInsightsServiceImpl(@Qualifier("userProfilesPersist") NerdPersistClient userProfilesPersist,
                                        @Qualifier("configPersist") NerdPersistClient configPersist) {
        this.userProfilesPersist = userProfilesPersist;
        this.configPersist = configPersist;
    }

    @Override
    public UserHomePageStatsEntity getUserHomePageStats(String id) {
        UserHomePageStatsEntity userHomePageStatsEntity = new UserHomePageStatsEntity();
        JsonObject userData = Commons.getUserProfileDocument(id, userProfilesPersist);
        updateUserHomePageStatsEntity(userData, userHomePageStatsEntity);
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
    }

    private void updateNumPeopleUsedNerdNudgeToday(UserHomePageStatsEntity userHomePageStatsEntity) {
        String currentDay = Commons.getDaystamp();
        userHomePageStatsEntity.setNumPeopleUsedNerdNudgeToday(userProfilesPersist.getCounter(currentDay + "_user_counts"));
    }

    private void updateQuote(UserHomePageStatsEntity userHomePageStatsEntity) {
        String currentDay = Commons.getDaystamp();
        if(quotesEntity == null || ! quotesEntity.getQuoteFetchDay().equals(currentDay)) {
            JsonObject quotesStatusDocument = configPersist.get("quotes_status");
            if(quotesStatusDocument == null)
                quotesStatusDocument = new JsonObject();

            JsonObject quotesObject = configPersist.get("quotes");
            if(! quotesStatusDocument.has("dayStamp") || ! quotesStatusDocument.get("dayStamp").getAsString().equals(currentDay)) {
                updateNewQuote(currentDay, quotesObject);
                saveConfigStatusDocument();
            }
            else {
                updateQuoteCache(quotesStatusDocument, quotesObject);
            }
        }

        userHomePageStatsEntity.setQuoteOfTheDay(quotesEntity.getQuote());
        StringBuilder quotesAuthorBuilder = new StringBuilder();
        quotesAuthorBuilder.append(quotesEntity.getAuthor());

        if(! quotesEntity.getAuthorCredentials().isEmpty()) {
            quotesAuthorBuilder.append(" (");
            quotesAuthorBuilder.append(quotesEntity.getAuthorCredentials());
            quotesAuthorBuilder.append(")");
        }
        userHomePageStatsEntity.setQuoteAuthor(quotesAuthorBuilder.toString());
    }

    private void updateQuoteCache(JsonObject quotesStatusDocument, JsonObject quotesObject) {
        String dayStamp = quotesStatusDocument.get("dayStamp").getAsString();
        String quoteId = quotesStatusDocument.get("quoteId").getAsString();
        JsonObject currentQuote = quotesObject.get(quoteId).getAsJsonObject();

        quotesEntity = new QuotesEntity(quoteId,
                currentQuote.get("quote").getAsString(),
                currentQuote.get("author").getAsString(),
                currentQuote.get("author_credentials").getAsString(),
                dayStamp
        );
    }

    private void saveConfigStatusDocument() {
        JsonObject configStatusDocument = new JsonObject();
        configStatusDocument.addProperty("dayStamp", quotesEntity.getQuoteFetchDay());
        configStatusDocument.addProperty("quoteId", quotesEntity.getQuotesId());
        configPersist.set("quotes_status", configStatusDocument);
    }

    private void updateNewQuote(String currentDay, JsonObject quotesObject) {
        int numQuotes = quotesObject.entrySet().size();
        JsonObject selectedQuoteForTheDay;
        int idSequence;
        while(true) {
            idSequence = random.nextInt(numQuotes) + 1;
            if(! quotesObject.has("q" + idSequence))
                continue;

            selectedQuoteForTheDay = quotesObject.get("q" + idSequence).getAsJsonObject();
            break;
        }

        quotesEntity = new QuotesEntity("q" + idSequence,
                selectedQuoteForTheDay.get("quote").getAsString(),
                selectedQuoteForTheDay.get("author").getAsString(),
                selectedQuoteForTheDay.get("author_credentials").getAsString(),
                currentDay
        );
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
