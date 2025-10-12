package com.criticalmass.core.services.dto;

import lombok.Builder;
import lombok.Value;

/**
 * Result of a sentiment analysis.
 * score: 0..1 (0 = negative, 0.5 = neutral, 1 = positive)
 * label: "positive" | "neutral" | "negative"
 * emoji: 😀 | 😐 | 😞
 */
@Value
@Builder(toBuilder = true)
public class SentimentResult {
    double score;
    String label;
    String emoji;
    int positiveCount;
    int negativeCount;
    int tokens;
}
