package de.tobiasbrandt.sellics.keywordrankassignment.service.amazoncompletion;

import java.util.List;

public class AmazonCompletionResponse {

	private String prefix;

	private List<Suggestion> suggestions;

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public List<Suggestion> getSuggestions() {
		return suggestions;
	}

	public void setSuggestions(List<Suggestion> suggestions) {
		this.suggestions = suggestions;
	}

	public static class Suggestion {
		private String value;

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}
}
