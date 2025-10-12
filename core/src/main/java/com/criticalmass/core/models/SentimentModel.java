package com.criticalmass.core.models;

import com.criticalmass.core.services.SentimentService;
import com.criticalmass.core.services.dto.SentimentResult;
import lombok.Getter;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;

/**
 * View model for the sentiment component.
 * Injects dialog fields, calls SentimentService, exposes simple getters for HTL.
 */
@Model(
        adaptables = SlingHttpServletRequest.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class SentimentModel {

    // ---- dialog fields ----
    @Getter
    @ValueMapValue(name = "text")
    private String text;

    @Getter
    @ValueMapValue(name = "showStats")
    @Default(booleanValues = false)
    private boolean showStats;

    // ---- service + result ----
    @OSGiService
    private SentimentService sentimentService;

    private SentimentResult result;

    @PostConstruct
    protected void init() {
        if (text != null && !text.trim().isEmpty() && sentimentService != null) {
            result = sentimentService.analyze(text);
        }
    }

    // ---- computed getters for HTL ----
    public boolean getHasResult() {
        return result != null;
    }

    public String getEmoji() {
        return result != null ? result.getEmoji() : "😐";
    }

    public String getLabel() {
        return result != null ? result.getLabel() : "neutral";
    }

    private static final double DEFAULT_SCORE = 0.5;

    public double getScore() {
        return result != null ? result.getScore() : DEFAULT_SCORE;
    }

    public int getPositiveCount() {
        return result != null ? result.getPositiveCount() : 0;
    }

    public int getNegativeCount() {
        return result != null ? result.getNegativeCount() : 0;
    }

    public int getTokens() {
        return result != null ? result.getTokens() : 0;
    }
}
