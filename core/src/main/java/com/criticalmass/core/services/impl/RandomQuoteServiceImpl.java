package com.criticalmass.core.services.impl;

import com.criticalmass.core.services.RandomQuoteService;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

@Component(service = RandomQuoteService.class)
@Slf4j
public class RandomQuoteServiceImpl implements RandomQuoteService {
    private static final String API_URL = "https://thequoteshub.com/api/";

    @Override
    public QuoteResult getRandomQuote() {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(API_URL);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String json = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                JSONObject obj = new JSONObject(json);
                String text = obj.optString("text", "No quote available.");
                String author = obj.optString("author", "Unknown");
                log.info("Quote: '{}' by '{}'", text, author);
                return new QuoteResult(text, author);
            } catch (Exception pe) {
                log.error("Failed to parse quote JSON", pe);
                return new QuoteResult("No quote available.", "Unknown");
            }
        } catch (Exception e) {
            log.error("Failed to fetch quote from API", e);
            return new QuoteResult("No quote available.", "Unknown");
        }
    }
}
