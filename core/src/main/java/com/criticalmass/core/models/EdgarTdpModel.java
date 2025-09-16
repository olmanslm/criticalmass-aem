package com.criticalmass.core.models;

import static org.apache.sling.api.resource.ResourceResolver.PROPERTY_RESOURCE_TYPE;

import com.criticalmass.core.services.EdgarTdpService;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Model(
        adaptables = Resource.class,
        adapters = EdgarTdpModel.class,
        resourceType = "criticalmass/components/edgarTdpCardComponent"
)
public class EdgarTdpModel {

    @OSGiService
    private EdgarTdpService edgarTdpService;

    private EdgarTdpCardsModel edgarTdpCardsModel;

    @ValueMapValue(name = PROPERTY_RESOURCE_TYPE, injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "No resourceType")
    protected String resourceType;

    @SlingObject
    private Resource currentResource;

    @SlingObject
    private ResourceResolver resourceResolver;

    @Inject
    private String cardName;

    @PostConstruct
    protected void init() {
        String currentPagePath = currentResource != null ? currentResource.getPath() : "";
        String message = "I'm starting!\n"
                + "Resource type is: " + resourceType + "\n"
                + "Current resource path is:  " + currentPagePath + "\n";

        // Example: call EdgarTdpService with dialog values
        if (edgarTdpService != null) {
            message += "EdgarTdpService available \n";
        }

        log.debug("Initialized EdgarTdpModel with message: {}", message);
    }

    public EdgarTdpCardsModel getCardInfo() {
        try {
            if (edgarTdpService != null) {
                ArrayList<EdgarTdpCardsModel> cardsList = edgarTdpService.getCardInfo(cardName);
                if (cardsList != null) {
                    return cardsList.get(0);
                }
            }
        } catch (Exception e) {
            String currentPagePath = currentResource != null ? currentResource.getPath() : "";
            String message = "I'm starting!\n"
                    + "Resource type is: " + resourceType + "\n"
                    + "Current resource path is:  " + currentPagePath + "\n";
            log.error("Failed to get card data {}", message, e);
        }

        return new EdgarTdpCardsModel();
    }

    public ArrayList<EdgarTdpCardsModel> getAllCardMatchesInfo() {
        try {
            if (edgarTdpService != null) {
                ArrayList<EdgarTdpCardsModel> cardsList = edgarTdpService.getCardInfo(cardName);
                if (cardsList != null) {
                    return cardsList;
                }
            }
        } catch (Exception e) {
            String currentPagePath = currentResource != null ? currentResource.getPath() : "";
            String message = "I'm starting!\n"
                    + "Resource type is: " + resourceType + "\n"
                    + "Current resource path is:  " + currentPagePath + "\n";
            log.error("Failed to get card data {}", message, e);
        }

        return new ArrayList<>();
    }
}