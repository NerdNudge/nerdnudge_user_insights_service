package com.neurospark.nerdnudge.userinsights;

import com.neurospark.nerdnudge.metrics.metrics.Metronome;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class NerdnudgeUserInsightsApplication {

	public static void main(String[] args) {
		Metronome.initiateMetrics(60000);
		SpringApplication.run(NerdnudgeUserInsightsApplication.class, args);
	}

	@Bean
	public GsonHttpMessageConverter gsonHttpMessageConverter() {
		return new GsonHttpMessageConverter();
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
