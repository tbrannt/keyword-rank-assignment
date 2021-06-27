package de.tobiasbrandt.sellics.keywordrankassignment.service.amazoncompletion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AmazonCompletionService {

	private static final Logger logger = LoggerFactory.getLogger(AmazonCompletionService.class);

	@Value("${endpoints.amazon.completion.url}")
	private String amazonCompletionUrl;

	@Value("${endpoints.amazon.completion.mid}")
	private String amazonCompletionMarketplaceId;

	@Value("${endpoints.amazon.completion.alias}")
	private String amazonCompletionAlias;

	@Value("${endpoints.amazon.completion.session-id}")
	private String amazonCompletionSessionId;

	@Autowired
	private CloseableHttpClient httpClient;

	private ObjectMapper objectMapper = new ObjectMapper();

	public AmazonCompletionService() {
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	/**
	 * Get suggestions from the amazon completion api for a keyword.
	 * 
	 * @param keyword
	 * @return a string list of suggestions
	 */
	public List<String> getSuggestions(String keyword) {
		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(amazonCompletionUrl)
				.queryParam("prefix", keyword)
				.queryParam("mid", amazonCompletionMarketplaceId)
				.queryParam("alias", amazonCompletionAlias)
				.queryParam("session-id", amazonCompletionSessionId);

		logger.debug("Getting amazon complete suggestions for {}", keyword);

		try {
			CloseableHttpResponse response = httpClient.execute(new HttpGet(uriBuilder.toUriString()));
			String responseBody = EntityUtils.toString(response.getEntity());
			AmazonCompletionResponse suggestionsRs = objectMapper.readValue(responseBody,
					AmazonCompletionResponse.class);

			return suggestionsRs.getSuggestions().stream().map(sug -> sug.getValue()).collect(Collectors.toList());
		} catch (IOException | ParseException e) {
			logger.error("failed to retrieve amazon completion response", e);
			return new ArrayList<>();
		}
	}

}
