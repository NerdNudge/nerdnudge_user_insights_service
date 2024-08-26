package com.neurospark.nerdnudge.userinsights.service.quotes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.neurospark.nerdnudge.couchbase.service.NerdPersistClient;
import com.neurospark.nerdnudge.userinsights.dto.QuotesEntity;
import com.neurospark.nerdnudge.userinsights.utils.Commons;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

@Service
public class QuotesService {
    static Map<String, QuotesEntity> allQuotes;
    private NerdPersistClient configPersist;
    private Random random = new Random();
    private QuotesEntity currentDayQuoteEntity = null;

    @Autowired
    public void Quotes(@Qualifier("configPersist") NerdPersistClient configPersist) {
        this.configPersist = configPersist;
        buildQuotes();
        updateCurrentDayQuote();
    }

    public QuotesEntity getQuoteOfTheDay() {
        String currentDay = Commons.getDaystamp();
        if(currentDayQuoteEntity == null || ! currentDayQuoteEntity.getQuoteFetchDay().equals(currentDay)) {
            updateCurrentDayQuote();
        }

        return currentDayQuoteEntity;
    }

    public QuotesEntity getQuoteById(String id) {
        if(! allQuotes.containsKey(id))
            return null;

        return allQuotes.get(id);
    }

    private void updateCurrentDayQuote() {
        String currentDay = Commons.getDaystamp();
        if(currentDayQuoteEntity == null || ! currentDayQuoteEntity.getQuoteFetchDay().equals(currentDay)) {
            JsonObject quotesStatusDocument = configPersist.get("quotes_status");
            if(quotesStatusDocument == null)
                quotesStatusDocument = new JsonObject();

            if(! quotesStatusDocument.has("dayStamp") || ! quotesStatusDocument.get("dayStamp").getAsString().equals(currentDay)) {
                updateCurrentQuoteOfTheDay();
                saveConfigStatusDocument();
            }
            else {
                updateQuoteCache(quotesStatusDocument);
            }
        }
    }

    private void updateQuoteCache(JsonObject quotesStatusDocument) {
        currentDayQuoteEntity = allQuotes.get(quotesStatusDocument.get("quoteId").getAsString());
        currentDayQuoteEntity.setQuoteFetchDay(Commons.getDaystamp());
    }

    private void saveConfigStatusDocument() {
        JsonObject configStatusDocument = new JsonObject();
        configStatusDocument.addProperty("dayStamp", currentDayQuoteEntity.getQuoteFetchDay());
        configStatusDocument.addProperty("quoteId", currentDayQuoteEntity.getQuotesId());
        configPersist.set("quotes_status", configStatusDocument);
    }

    private void buildQuotes() {
        allQuotes = new HashMap<>();
        JsonObject quotesObject = configPersist.get("quotes");
        Iterator<Map.Entry<String, JsonElement>> quotesIterator = quotesObject.entrySet().iterator();
        while(quotesIterator.hasNext()) {
            Map.Entry<String, JsonElement> thisEntry = quotesIterator.next();
            String thisQuotesId = thisEntry.getKey();
            JsonObject thisQuotesObject = thisEntry.getValue().getAsJsonObject();

            String thisQuote = thisQuotesObject.get("quote").getAsString();
            String thisQuoteAuthor = thisQuotesObject.get("author").getAsString();
            String thisQuoteAuthorCreds = thisQuotesObject.get("author_credentials").getAsString();

            QuotesEntity thisQuotesEntity = new QuotesEntity(thisQuotesId, thisQuote, thisQuoteAuthor, thisQuoteAuthorCreds, "0");
            allQuotes.put(thisQuotesId, thisQuotesEntity);
        }
    }

    private void updateCurrentQuoteOfTheDay() {
        int numQuotes = allQuotes.entrySet().size();
        int idSequence;
        while(true) {
            idSequence = random.nextInt(numQuotes) + 1;
            if(! allQuotes.containsKey("q" + idSequence))
                continue;

            currentDayQuoteEntity = allQuotes.get("q" + idSequence);
            currentDayQuoteEntity.setQuoteFetchDay(Commons.getDaystamp());
            break;
        }
    }
}
