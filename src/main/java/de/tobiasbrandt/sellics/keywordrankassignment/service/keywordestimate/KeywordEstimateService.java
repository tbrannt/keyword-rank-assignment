package de.tobiasbrandt.sellics.keywordrankassignment.service.keywordestimate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tobiasbrandt.sellics.keywordrankassignment.service.amazoncompletion.AmazonCompletionService;

@Service
public class KeywordEstimateService {

	private static final int MAX_REQUESTS = 10;

	private static final Logger logger = LoggerFactory.getLogger(KeywordEstimateService.class);

	private static final int EXACT_MATCH_SCORE = 9;

	private static final int CORRECTED_MATCH_SCORE = 5;

	@Autowired
	private AmazonCompletionService amazonCompletionService;

	private ExecutorService executor = Executors.newFixedThreadPool(MAX_REQUESTS);

	/**
	 * Gets the search volume score for a keyword by consulting the amazon
	 * autocompletion api.<br>
	 * 
	 * The score will be an integer between 0 and 100 whereas 0 mean zero search
	 * relevance and 100 means maximum search relevance.
	 * 
	 * @param keyword
	 * @return search volume score of the keyword
	 * @throws InterruptedException
	 */
	public int getKeywordScore(String keyword) throws InterruptedException {
		List<String> initialSuggestions = amazonCompletionService.getSuggestions(keyword);
		if (initialSuggestions.size() == 0) {
			logger.debug("Did not find any result for keyword '{}' Returning score: 0", keyword);
			return 0;
		}

		logger.debug("Retrieved {} initial amazon suggestions for {}: {}", initialSuggestions.size(), keyword,
				initialSuggestions);

		String correctedPrefix = getPossiblyCorrectedSearchPrefix(keyword, initialSuggestions.get(0));
		logger.debug("Corrected prefix is: {}", correctedPrefix);

		int resultSizeScore = initialSuggestions.size();
		Integer subscores = getScoreForSubRequests(initialSuggestions, keyword, correctedPrefix);

		return resultSizeScore + subscores;
	}

	private Integer getScoreForSubRequests(List<String> initialSuggestions, String keyword, String correctedPrefix)
			throws InterruptedException {
		List<Callable<Integer>> getScoreTasks = new ArrayList<>();
		for (int i = 0; i < Math.min(initialSuggestions.size(), MAX_REQUESTS); i++) {
			String suggestion = initialSuggestions.get(i);

			Callable<Integer> scoreTask = () -> {
				if (suggestion.equalsIgnoreCase(keyword)) {
					return EXACT_MATCH_SCORE;
				}
				if (suggestion.equalsIgnoreCase(correctedPrefix)) {
					return CORRECTED_MATCH_SCORE;
				}

				String suggestionWithoutKeyword = getSuggestionWithoutKeyword(suggestion, keyword, correctedPrefix);
				List<String> subSuggestions = amazonCompletionService.getSuggestions(suggestionWithoutKeyword);

				if (subSuggestions.stream().anyMatch(sug -> sug.toLowerCase().contains(keyword.toLowerCase()))) {
					logger.info("found exact match in {}", subSuggestions);
					return EXACT_MATCH_SCORE;
				}
				if (subSuggestions.stream()
						.anyMatch(sug -> sug.toLowerCase().contains(correctedPrefix.toLowerCase()))) {
					logger.info("found corrected match in {}", subSuggestions);
					return CORRECTED_MATCH_SCORE;
				}

				return 0;
			};

			getScoreTasks.add(scoreTask);
		}

		return executeAndGetTotalSubScores(getScoreTasks);
	}

	/**
	 * Extracts the keyword/prefix or corrected prefix from the suggestion. If e.g.
	 * the keyword is a substring of the corrected prefix or the other way round
	 * then the longer one of both strings will be replaced.
	 */
	private String getSuggestionWithoutKeyword(String suggestion, String keyword, String correctedPrefix) {
		suggestion = suggestion.toLowerCase();
		keyword = keyword.toLowerCase();
		correctedPrefix = correctedPrefix.toLowerCase();

		if (suggestion.contains(keyword) && suggestion.contains(correctedPrefix)) {
			return suggestion.replace(keyword.length() > correctedPrefix.length() ? keyword : correctedPrefix, "")
					.trim();
		}

		if (suggestion.contains(keyword)) {
			return suggestion.replace(keyword, "").trim();
		}

		if (suggestion.contains(correctedPrefix)) {
			return suggestion.replace(correctedPrefix, "").trim();
		}

		return suggestion;
	}

	private Integer executeAndGetTotalSubScores(List<Callable<Integer>> getScoreTasks) throws InterruptedException {
		List<Future<Integer>> subScoresFuture = executor.invokeAll(getScoreTasks);
		Integer subscores = subScoresFuture.stream()
				.map(f -> {
					try {
						return f.get();
					} catch (InterruptedException | ExecutionException e) {
						return 0;
					}
				})
				.reduce(0, (a, b) -> a + b);
		return subscores;
	}

	private String getPossiblyCorrectedSearchPrefix(String prefix, String suggestion) {
		String[] prefixArray = prefix.split(" ");
		String[] suggestionArray = suggestion.split(" ");

		String correctedPrefix = "";
		for (int i = 0; i < prefixArray.length; i++) {
			correctedPrefix += suggestionArray[i] + " ";
		}

		return correctedPrefix.trim();
	}

}
