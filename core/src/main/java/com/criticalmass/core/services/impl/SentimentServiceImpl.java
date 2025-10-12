package com.criticalmass.core.services.impl;

import com.criticalmass.core.services.SentimentService;
import com.criticalmass.core.services.dto.SentimentResult;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Component(service = SentimentService.class)
public class SentimentServiceImpl implements SentimentService {

    // Named category so we can route logs to a custom file
    private static final Logger LOG = LoggerFactory.getLogger("com.criticalmass.nlp.sentiment");

    private static final Set<String> POSITIVE = new HashSet<>(Arrays.asList(
        // EN
        "love", "like", "great", "good", "awesome", "amazing", "happy", "excellent", "win", "cool", "best", "yay", "nice", "sweet", "bravo",
        // ES (a few for fun)
        "bueno", "genial", "feliz", "excelente", "maravilloso"
    ));
    private static final Set<String> NEGATIVE = new HashSet<>(Arrays.asList(
        // EN
        "hate", "dislike", "bad", "terrible", "awful", "sad", "angry", "worse", "worst", "slow", "bug", "issue", "broken", "meh", "nope", "ugly",
        // ES
        "malo", "horrible", "lento", "triste", "feo"
    ));

    private static final double NEUTRAL_SENTIMENT_SCORE = 0.5;

    @Override
    public SentimentResult analyze(final String text) {
        if (text == null || text.trim().isEmpty()) {
            LOG.info("Sentiment: empty text → neutral");
            return SentimentResult.builder()
                .score(NEUTRAL_SENTIMENT_SCORE)
                .label("neutral")
                .emoji("😐")
                .positiveCount(0)
                .negativeCount(0)
                .tokens(0)
                .build();
        }

        // normalize/strip accents, lowercase
        String norm = Normalizer.normalize(text, Normalizer.Form.NFD)
                                 .replaceAll("\\p{M}", "")
                                 .toLowerCase(Locale.ROOT);

        // simple tokenization on non letters/digits
        String[] rawTokens = norm.split("[^\\p{IsAlphabetic}\\p{IsDigit}]+");
        int tokens = 0;

        int pos = 0;
        int neg = 0;
        for (String t : rawTokens) {
            if (t.isEmpty()) {
                continue;
            }
            tokens++;
            if (POSITIVE.contains(t)) {
                pos++;
            } else if (NEGATIVE.contains(t)) {
                neg++;
            }
        }

        double score01; // 0..1
        String label;
        String emoji;

        final double sentimentThreshold = 0.25;

        if (pos + neg == 0) {
            // no sentiment words: neutral
            score01 = NEUTRAL_SENTIMENT_SCORE;
            label = "neutral";
            emoji = "😐";
        } else {
            // raw in [-1,1]
            double raw = (pos - neg) / (double) (pos + neg);
            score01 = (raw + 1.0) / 2.0; // map to 0..1
            if (raw >= sentimentThreshold) {
                label = "positive";
                emoji = "😀";
            } else if (raw <= -sentimentThreshold) {
                label = "negative";
                emoji = "😞";
            } else {
                label = "neutral";
                emoji = "😐";
            }
            LOG.debug("Sentiment tokens={} pos={} neg={} raw={} score01={}",
                      tokens, pos, neg, String.format(Locale.ROOT, "%.3f", raw),
                      String.format(Locale.ROOT, "%.3f", score01));
        }

        // brief sample (don’t spam logs with long texts)
        final int maxSampleLength = 80;
        final int maxSampleSubstring = 77;
        String sample = text.length() > maxSampleLength ? text.substring(0, maxSampleSubstring) + "..." : text;
        LOG.info("Sentiment [{}] {} for sample: {}", label, emoji, sample);

        return SentimentResult.builder()
            .score(score01)
            .label(label)
            .emoji(emoji)
            .positiveCount(pos)
            .negativeCount(neg)
            .tokens(tokens)
            .build();
    }
}
