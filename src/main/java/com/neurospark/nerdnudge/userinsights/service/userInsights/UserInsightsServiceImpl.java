package com.neurospark.nerdnudge.userinsights.service.userInsights;

import com.google.gson.JsonObject;
import com.neurospark.nerdnudge.couchbase.service.NerdPersistClient;
import com.neurospark.nerdnudge.userinsights.utils.Commons;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

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
        return Commons.getUserProfileDocument(userId, userProfilesPersist);
    }
}
