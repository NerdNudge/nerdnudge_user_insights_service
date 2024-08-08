package com.neurospark.nerdnudge.userinsights;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.GsonHttpMessageConverter;

@SpringBootApplication
public class NerdnudgeUserInsightsApplication {

	public static void main(String[] args) {
		SpringApplication.run(NerdnudgeUserInsightsApplication.class, args);
	}

	@Bean
	public GsonHttpMessageConverter gsonHttpMessageConverter() {
		return new GsonHttpMessageConverter();
	}
}
