package de.tobiasbrandt.sellics.keywordrankassignment.service.keywordestimate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.metrics.ApplicationStartup;
import org.springframework.stereotype.Service;

@Service
public class KeywordEstimateService {

	private static final Logger logger = LoggerFactory.getLogger(ApplicationStartup.class);

	public int getKeywordScore(String keyword) {
		// TODO Auto-generated method stub
		return 42;
	}

}
