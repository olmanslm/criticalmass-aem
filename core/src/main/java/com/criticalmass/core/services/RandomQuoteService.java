package com.criticalmass.core.services;

public interface RandomQuoteService {
    /**
     * Fetches a random quote from the public API.
     * @return QuoteResult containing text and author, or fallback if error occurs.
     */
    QuoteResult getRandomQuote();

    /**
     * Simple DTO for quote result.
     */
    class QuoteResult {
        private final String text;
        private final String author;

        public QuoteResult(String text, String author) {
            this.text = text;
            this.author = author;
        }

        public String getText() {
            return text;
        }

        public String getAuthor() {
            return author;
        }
    }
}
