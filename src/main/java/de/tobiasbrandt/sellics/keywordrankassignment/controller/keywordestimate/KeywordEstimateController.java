package de.tobiasbrandt.sellics.keywordrankassignment.controller.keywordestimate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.tobiasbrandt.sellics.keywordrankassignment.service.keywordestimate.KeywordEstimateService;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/estimate")
public class KeywordEstimateController {

	@Autowired
	private KeywordEstimateService keywordEstimateService;

	@ApiOperation("Gets an estimate of the keyword's popularity on amazon")
	@GetMapping
	public KeywordEstimateResponse getKeywordEstimate(
			@RequestParam(value = "keyword", required = true) String keyword) throws InterruptedException {
		int estimate = keywordEstimateService.getKeywordScore(keyword);
		return new KeywordEstimateResponse(keyword, estimate);
	}

}
