package com.criticalmass.core.models;

import com.criticalmass.core.services.GreetingService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;

@Model(
    adaptables = SlingHttpServletRequest.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class MyDemoModel {

    @SlingObject
    private SlingHttpServletRequest request;

    @SlingObject
    private Resource resource;

    // Dialog fields (names match ./text and ./number in the dialog)
    @ValueMapValue(name = "text")
    private String text;

    // AEM numberfield typically stores a Long
    @ValueMapValue(name = "number")
    private Long number;

    @OSGiService
    private GreetingService greetingService;

    private String message;
    private String greetingServiceMessage;

    @PostConstruct
    protected void init() {
        String name = (text != null && !text.isEmpty()) ? text : "Dialog User";
        int n = (number != null) ? number.intValue() : 0;

        // simple computed message
        this.message = "Model initialized for '" + name + "' (number=" + n + ")";

        // call your service (safe if service is missing)
        if (greetingService != null) {
            this.greetingServiceMessage = greetingService.getMessage(name, n);
        } else {
            this.greetingServiceMessage = "GreetingService unavailable";
        }
    }

    // ---- getters used by HTL ----
    public String getMessage() {
        return message;
    }

    public String getGreetingService() {
        return greetingServiceMessage;
    }

    public String getText() {
        return text;
    }

    public Long getNumber() {
        return number;
    }
}
