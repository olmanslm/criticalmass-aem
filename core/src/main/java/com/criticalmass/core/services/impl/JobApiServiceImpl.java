package com.criticalmass.core.services.impl;

import com.criticalmass.core.services.JobApiService;
import com.criticalmass.core.services.dto.ApiResponse;
import com.criticalmass.core.services.dto.JobListing;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component(service = JobApiService.class)
@Designate(ocd = JobApiConfiguration.class)
public class JobApiServiceImpl implements JobApiService {

    private static final Logger LOG = LoggerFactory.getLogger(JobApiServiceImpl.class);
    private static final int HTTP_STATUS_SUCCESS_MIN = 200;
    private static final int HTTP_STATUS_SUCCESS_MAX = 300;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private volatile String apiBaseUrl;
    private volatile int connectionTimeout;
    private volatile int readTimeout;

    @Activate
    @Modified
    protected void activate(final JobApiConfiguration configuration) {
        this.apiBaseUrl = normalizeBaseUrl(configuration.apiBaseUrl());
        this.connectionTimeout = Math.max(1000, configuration.connectionTimeout());
        this.readTimeout = Math.max(1000, configuration.readTimeout());
        LOG.info("Job API service configured with base URL {} (connect timeout {} ms, read timeout {} ms)",
            this.apiBaseUrl, this.connectionTimeout, this.readTimeout);
    }

    private String normalizeBaseUrl(final String rawBaseUrl) {
        String fallback = "https://jobdataapi.com/api/jobs";
        if (rawBaseUrl == null || rawBaseUrl.trim().isEmpty()) {
            return fallback;
        }
        String normalized = rawBaseUrl.trim();
        while (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized.isEmpty() ? fallback : normalized;
    }

    private String buildJobsUrl(final String location, final String company) {
        StringBuilder url = new StringBuilder(apiBaseUrl).append("/");
        boolean hasQuery = false;

        if (location != null && !location.trim().isEmpty()) {
            url.append("location=").append(URLEncoder.encode(location.trim(), StandardCharsets.UTF_8));
            hasQuery = true;
        }

        if (company != null && !company.trim().isEmpty()) {
            url.append(hasQuery ? "&" : "?")
                .append("company=")
                .append(URLEncoder.encode(company.trim(), StandardCharsets.UTF_8));
        } else if (hasQuery) {
            // Prefix the first query parameter when only location is set.
            int queryStart = url.indexOf("location=");
            if (queryStart > -1) {
                url.insert(queryStart, "?");
            }
        }

        return url.toString();
    }

    private CloseableHttpClient createHttpClient() {
        RequestConfig requestConfig = RequestConfig.custom()
            .setConnectTimeout(connectionTimeout)
            .setConnectionRequestTimeout(connectionTimeout)
            .setSocketTimeout(readTimeout)
            .build();

        return HttpClients.custom()
            .setDefaultRequestConfig(requestConfig)
            .build();
    }

    @Override
    public ApiResponse get(final String url) {
        try (CloseableHttpClient client = createHttpClient()) {
            HttpGet request = new HttpGet(url);
            request.setHeader("Content-Type", "application/json");

            try (CloseableHttpResponse response = client.execute(request)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode >= HTTP_STATUS_SUCCESS_MIN && statusCode < HTTP_STATUS_SUCCESS_MAX) {
                    return ApiResponse.builder()
                        .success(true)
                        .data(responseBody)
                        .statusCode(statusCode)
                        .build();
                } else {
                    LOG.error("API request failed with status: {}", statusCode);
                    return ApiResponse.builder()
                        .success(false)
                        .statusCode(statusCode)
                        .errorMessage("Request failed with status: " + statusCode)
                        .build();
                }
            }
        } catch (Exception e) {
            LOG.error("Error making API request to: {}", url, e);
            return ApiResponse.builder()
                .success(false)
                .errorMessage(e.getMessage())
                .build();
        }
    }

    @Override
    public ApiResponse post(final String url, final String jsonPayload) {
        try (CloseableHttpClient client = createHttpClient()) {
            HttpPost request = new HttpPost(url);
            request.setHeader("Content-Type", "application/json");
            request.setEntity(new StringEntity(jsonPayload));

            try (CloseableHttpResponse response = client.execute(request)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode >= HTTP_STATUS_SUCCESS_MIN && statusCode < HTTP_STATUS_SUCCESS_MAX) {
                    return ApiResponse.builder()
                        .success(true)
                        .data(responseBody)
                        .statusCode(statusCode)
                        .build();
                } else {
                    return ApiResponse.builder()
                        .success(false)
                        .statusCode(statusCode)
                        .errorMessage("Request failed with status: " + statusCode)
                        .build();
                }
            }
        } catch (Exception e) {
            LOG.error("Error making POST request to: {}", url, e);
            return ApiResponse.builder()
                .success(false)
                .errorMessage(e.getMessage())
                .build();
        }
    }

    @Override
    public List<JobListing> fetchJobs() {
        ApiResponse response = get(buildJobsUrl(null, null));

        if (response.isSuccess()) {
            try {
                JsonNode jsonNode = objectMapper.readTree(String.valueOf(response.getData()));
                JsonNode resultsArray = jsonNode.path("results");
                if (!resultsArray.isArray() && jsonNode.isArray()) {
                    resultsArray = jsonNode;
                }

                List<JobListing> jobs = new ArrayList<>();
                for (JsonNode jobNode : resultsArray) {
                    jobs.add(toJobListing(jobNode));
                }
                LOG.info("Successfully fetched {} jobs", jobs.size());
                return jobs;
            } catch (Exception e) {
                LOG.error("Error parsing jobs data", e);
                return new ArrayList<>();
            }
        }

        LOG.warn("Failed to fetch jobs: {}", response.getErrorMessage());
        return new ArrayList<>();
    }

    @Override
    public List<JobListing> fetchJobsByLocation(final String location) {
        ApiResponse response = get(buildJobsUrl(location, null));

        if (response.isSuccess()) {
            try {
                JsonNode jsonNode = objectMapper.readTree(String.valueOf(response.getData()));
                List<JobListing> jobs = new ArrayList<>();

                JsonNode resultsArray = jsonNode.path("results");
                if (!resultsArray.isArray() && jsonNode.isArray()) {
                    resultsArray = jsonNode;
                }

                for (JsonNode jobNode : resultsArray) {
                    JobListing job = toJobListing(jobNode);
                    if (containsIgnoreCase(job.getLocation(), location)) {
                        jobs.add(job);
                    }
                }

                LOG.info("Successfully fetched {} jobs for location: {}", jobs.size(), location);
                return jobs;
            } catch (Exception e) {
                LOG.error("Error parsing jobs data for location: {}", location, e);
                return new ArrayList<>();
            }
        }

        LOG.warn("Failed to fetch jobs for location {}: {}", location, response.getErrorMessage());
        return new ArrayList<>();
    }

    @Override
    public List<JobListing> fetchJobsByCompany(final String company) {
        ApiResponse response = get(buildJobsUrl(null, company));

        if (response.isSuccess()) {
            try {
                JsonNode jsonNode = objectMapper.readTree(String.valueOf(response.getData()));
                List<JobListing> jobs = new ArrayList<>();

                JsonNode resultsArray = jsonNode.path("results");
                if (!resultsArray.isArray() && jsonNode.isArray()) {
                    resultsArray = jsonNode;
                }

                for (JsonNode jobNode : resultsArray) {
                    JobListing job = toJobListing(jobNode);
                    if (containsIgnoreCase(job.getCompanyName(), company)) {
                        jobs.add(job);
                    }
                }

                LOG.info("Successfully fetched {} jobs for company: {}", jobs.size(), company);
                return jobs;
            } catch (Exception e) {
                LOG.error("Error parsing jobs data for company: {}", company, e);
                return new ArrayList<>();
            }
        }

        LOG.warn("Failed to fetch jobs for company {}: {}", company, response.getErrorMessage());
        return new ArrayList<>();
    }

    private JobListing toJobListing(final JsonNode jobNode) {
        JsonNode companyNode = jobNode.path("company");
        JsonNode typesArray = jobNode.path("types");

        String jobType = "";
        if (typesArray.isArray() && typesArray.size() > 0) {
            jobType = typesArray.get(0).path("name").asText("");
        }

        String applicationUrl = jobNode.path("application_url").asText("");
        if (applicationUrl.isEmpty()) {
            applicationUrl = jobNode.path("url").asText("");
        }

        return JobListing.builder()
            .id(jobNode.path("id").asText(""))
            .title(jobNode.path("title").asText(""))
            .companyName(companyNode.path("name").asText(""))
            .companyLinkedInUrl(companyNode.path("linkedin_url").asText(""))
            .location(jobNode.path("location").asText(""))
            .description(jobNode.path("description").asText(""))
            .jobType(jobType)
            .url(applicationUrl)
            .build();
    }

    private boolean containsIgnoreCase(final String source, final String filter) {
        if (filter == null || filter.trim().isEmpty()) {
            return true;
        }
        return source != null && source.toLowerCase().contains(filter.trim().toLowerCase());
    }
}
