package de.tobiasbrandt.sellics.keywordrankassignment;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeywordRankAssignmentConfig {

	@Bean
	public CloseableHttpClient httpClient() {
		return HttpClientBuilder.create().build();
	}

}
