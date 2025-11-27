package com.criticalmass.core.models;

import com.criticalmass.core.services.RandomQuoteService;
import com.criticalmass.core.services.GreetingService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import javax.annotation.PostConstruct;

@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class RandomQuoteModel {
    @OSGiService
    private RandomQuoteService randomQuoteService;

    @OSGiService
    private GreetingService greetingService;

    @ValueMapValue
    private String text;

    @ValueMapValue
    private String author;

    @ValueMapValue
    private String userQuote;

    private String serviceText;
    private String serviceAuthor;

    @PostConstruct
    protected void init() {
        if (randomQuoteService != null) {
            RandomQuoteService.QuoteResult result = randomQuoteService.getRandomQuote();
            if (result != null) {
                this.serviceText = result.getText();
                this.serviceAuthor = result.getAuthor();
            }
        }
        
        // If no dialog properties, use service values
        if (text == null || text.isEmpty()) {
            this.text = serviceText;
        }
        if (author == null || author.isEmpty()) {
            this.author = serviceAuthor;
        }
    }

    public String getText() {
        return text;
    }

    public String getAuthor() {
        return author;
    }

    public String getServiceText() {
        return serviceText;
    }

    public String getServiceAuthor() {
        return serviceAuthor;
    }

    public String getGreetingService() {
        return greetingService.getMessage("World", 123);
    }

    public String getUserQuote() {
        return userQuote;
    }
}
