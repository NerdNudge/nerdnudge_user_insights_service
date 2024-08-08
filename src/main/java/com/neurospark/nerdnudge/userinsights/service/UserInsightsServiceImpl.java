package com.neurospark.nerdnudge.userinsights.service;

import com.google.gson.JsonObject;
import com.neurospark.nerdnudge.couchbase.service.NerdPersistClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UserInsightsServiceImpl implements UserInsightsService {

    private NerdPersistClient configPersist;
    private NerdPersistClient userProfilesPersist;

    @Autowired
    public void UserInsightsServiceImpl(@Qualifier("configPersist") NerdPersistClient configPersist,
                                        @Qualifier("userProfilesPersist") NerdPersistClient userProfilesPersist) {
        this.configPersist = configPersist;
        this.userProfilesPersist = userProfilesPersist;
    }

    @Override
    public JsonObject getUserInsights(String userId) {
        return getUserProfileDocument(userId);
    }

    private JsonObject getUserProfileDocument(String userId) {
        JsonObject userData = userProfilesPersist.get(userId);
        if(userData == null) {
            userData = new JsonObject();
            userData.addProperty("registrationDate", Instant.now().getEpochSecond());
            userData.addProperty("type", "userProfile");
            userData.addProperty("accountType", "freemium");
            userData.addProperty("accountStartDate", com.neurospark.nerdnudge.useractivity.utils.Commons.getInstance().getDaystamp());
        }

        System.out.println("user data returned: " + userData);
        return userData;
    }
}
