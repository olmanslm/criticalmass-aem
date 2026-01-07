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
public class RobertoDemoModel {

    @SlingObject
    private SlingHttpServletRequest request;

    @SlingObject
    private Resource resource;

    @ValueMapValue(name = "text")
    private String text;

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

        this.message = "Model initialized for '" + name + "' (number=" + n + ")";

        if (greetingService != null) {
            this.greetingServiceMessage = greetingService.getMessage(name, n);
        } else {
            this.greetingServiceMessage = "GreetingService unavailable";
        }
    }
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