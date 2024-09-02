package com.neurospark.nerdnudge.userinsights.service.userInsights;

import com.neurospark.nerdnudge.userinsights.dto.insights.UserInsightsEntity;
import com.neurospark.nerdnudge.userinsights.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class UserRanksAndScoresService {
    private RestTemplate restTemplate;
    private final String userRankingServiceBaseURL;

    private String userRanksAndScoresUrlPath = "/getUserRanksAndScores/";

    @Autowired
    public UserRanksAndScoresService(RestTemplate restTemplate,
                                     @Value("${user.ranking.service.api.baseurl:http://localhost:9096/api/nerdnudge/userranks}") String userRankingServiceBaseURL) {
        this.restTemplate = restTemplate;
        this.userRankingServiceBaseURL = userRankingServiceBaseURL;
    }

    public void updateUserRanksAndScores(UserInsightsEntity userInsightsEntity, String userId) {
        ApiResponse<LinkedHashMap> response = restTemplate.getForObject(userRankingServiceBaseURL + userRanksAndScoresUrlPath + userId, ApiResponse.class);

        LinkedHashMap ranksAndScores = response.getData();
        userInsightsEntity.setRankings((Map<String, Integer>) ranksAndScores.get("topicsRank"));
        userInsightsEntity.setScores((Map<String, Double>) ranksAndScores.get("topicsScore"));
    }
}
