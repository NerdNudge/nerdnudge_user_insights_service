package com.neurospark.nerdnudge.userinsights.service.userHomePage;

import com.neurospark.nerdnudge.couchbase.service.NerdPersistClient;
import com.neurospark.nerdnudge.userinsights.dto.UserHomePageStatsEntity;
import com.neurospark.nerdnudge.userinsights.utils.Commons;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class UserHomePageStatsServiceImpl implements UserHomePageStatsService{

    public NerdPersistClient userProfilesPersist;

    @Autowired
    public void UserInsightsServiceImpl(@Qualifier("userProfilesPersist") NerdPersistClient userProfilesPersist) {
        this.userProfilesPersist = userProfilesPersist;
    }

    @Override
    public UserHomePageStatsEntity getUserHomePageStats(String id) {
        return new UserHomePageStatsEntity(Commons.getUserProfileDocument(id, userProfilesPersist), userProfilesPersist);
    }
}
