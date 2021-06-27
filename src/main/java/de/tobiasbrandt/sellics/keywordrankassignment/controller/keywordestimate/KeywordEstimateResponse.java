package de.tobiasbrandt.sellics.keywordrankassignment.controller.keywordestimate;

public class KeywordEstimateResponse {

	private String keyword;

	private Integer score;

	public KeywordEstimateResponse(String keyword, Integer score) {
		this.keyword = keyword;
		this.score = score;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

}
