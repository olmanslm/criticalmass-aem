package com.criticalmass.core.services.impl;

import com.criticalmass.core.services.EdgarTdpService;
import com.criticalmass.core.models.EdgarTdpCardsModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;

/**
 * OSGi service implementation.
 * - @Component registers this as an OSGi service.
 * - The 'service' attribute tells OSGi which interface it implements.
 * - @Slf4j is a Lombok annotation that gives us a 'log' instance without writing boilerplate.
 */
@Component(service = EdgarTdpService.class, immediate = true)
@Designate(ocd = EdgarTdpServiceImpl.Config.class)
@Slf4j
public class EdgarTdpServiceImpl implements EdgarTdpService {

    @ObjectClassDefinition(name = "Edgar TDP Service Configuration", description = "Dynamic configuration for EdgarTdpService")
    public @interface Config {
        String baseUrl() default "https://api.magicthegathering.io/v1/cards";
    }

    private String baseUrl;

    @Activate
    @Modified
    protected void activateOrUpdate(Config config) {
        this.baseUrl = config.baseUrl();
        log.info("Configuration updated: baseUrl={}", baseUrl);
    }

    @Override
    public ArrayList<EdgarTdpCardsModel> getCardInfo(String cardName) {
        String url = String.format("%s?name=%s", baseUrl, java.net.URLEncoder.encode(cardName, java.nio.charset.StandardCharsets.UTF_8));

        log.debug("EdgarTdpService Processing started for url: {}", url);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                if (response.getStatusLine().getStatusCode() == 200) {
                    String jsonResponse = EntityUtils.toString(response.getEntity());
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                    // Adjust deserialization to match the JSON structure
                    JsonNode rootNode = objectMapper.readTree(jsonResponse);
                    JsonNode cardsNode = rootNode.get("cards");

                    if (cardsNode != null && cardsNode.isArray()) {
                        return objectMapper.readValue(
                                cardsNode.toString(),
                                objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, EdgarTdpCardsModel.class)
                        );
                    } else {
                        log.error("No 'cards' array found in the response");
                    }
                } else {
                    log.error("Failed to fetch card info. HTTP Status: {}", response.getStatusLine().getStatusCode());
                }
            }
        } catch (Exception e) {
            log.error("Error fetching card info", e);
        }

        return new ArrayList<>();
    }
}