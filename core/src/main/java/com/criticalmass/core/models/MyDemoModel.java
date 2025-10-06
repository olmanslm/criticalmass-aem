package com.criticalmass.core.models;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.criticalmass.core.services.GreetingService;

@Model(
    adaptables = SlingHttpServletRequest.class,
    resourceType = "criticalmass/components/mydemo",
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class MyDemoModel {

    private static final double DEFAULT_HST = 13.0;

    @ValueMapValue
    private String  text;

    @ValueMapValue
    private String  name;
    @ValueMapValue private Integer number;

    @ValueMapValue
    private Double  hstRate;

    @ValueMapValue
    private Integer itemCount;

    @ValueMapValue
    private Double  pricePerItem;

    @OSGiService
    private GreetingService greetingService;

    private String message;
    private String serviceMessage;

    @PostConstruct
    protected void init() {
        int num = number != null ? number : 0;
        message = "Text=" + (text != null ? text : "") + ", Number=" + num;

        if (greetingService != null) {
            String n = (name != null && !name.isBlank()) ? name : "Demo User";
            serviceMessage = greetingService.getMessage(n, num);
        } else {
            serviceMessage = "GreetingService not available";
        }

        if (hstRate == null)      hstRate = DEFAULT_HST;
        if (itemCount == null)    itemCount = 1;
        if (pricePerItem == null) pricePerItem = 0.00;
    }

    // getters used by HTL
    public String getMessage() {
        return message;
    }
    public String getGreetingService() {
        return serviceMessage;
    }

    public String getText() {
        return text;
    }
    public String getName() {
        return name;
    }
    public Integer getNumber() {
        return number;
    }

    public double getHstRate() {
        return hstRate != null ? hstRate : DEFAULT_HST;
    }
    public int getItemCount() {
        return itemCount != null ? itemCount : 1;
    }
    public double getPricePerItem() {
        return pricePerItem != null ? pricePerItem : 0.0;
    }
}
