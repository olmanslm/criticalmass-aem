package com.criticalmass.core.services.impl;

import com.criticalmass.core.services.JobApiService;
import com.criticalmass.core.services.dto.ApiResponse;
import com.criticalmass.core.services.dto.JobListing;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Component(service = JobApiService.class)
@Designate(ocd = JobApiConfiguration.class)
public class JobApiServiceImpl implements JobApiService {
    
    private static final Logger LOG = LoggerFactory.getLogger(JobApiServiceImpl.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // Default job API endpoint
    private static final String DEFAULT_API_BASE_URL = "https://jobs.github.com";
    
    @Override
    public ApiResponse get(String url) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            request.setHeader("Content-Type", "application/json");
            
            try (CloseableHttpResponse response = client.execute(request)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                int statusCode = response.getStatusLine().getStatusCode();
                
                if (statusCode >= 200 && statusCode < 300) {
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
    public ApiResponse post(String url, String jsonPayload) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(url);
            request.setHeader("Content-Type", "application/json");
            request.setEntity(new StringEntity(jsonPayload));
            
            try (CloseableHttpResponse response = client.execute(request)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                int statusCode = response.getStatusLine().getStatusCode();
                
                if (statusCode >= 200 && statusCode < 300) {
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
        String url = DEFAULT_API_BASE_URL + "/positions.json";
        ApiResponse response = get(url);
        
        if (response.isSuccess()) {
            try {
                JsonNode jsonNode = objectMapper.readTree(response.getData().toString());
                List<JobListing> jobs = new ArrayList<>();
                
                // API returns results array
                JsonNode resultsArray = jsonNode.isArray() ? jsonNode : jsonNode.get("results");
                if (resultsArray == null) {
                    resultsArray = jsonNode;
                }
                
                for (JsonNode jobNode : resultsArray) {
                    // Extract company information
                    JsonNode companyNode = jobNode.path("company");
                    String companyName = companyNode.path("name").asText();
                    String linkedInUrl = companyNode.path("linkedin_url").asText();
                    
                    // Extract job type from types array
                    String jobType = "";
                    JsonNode typesNode = jobNode.path("types");
                    if (typesNode.isArray() && typesNode.size() > 0) {
                        jobType = typesNode.get(0).path("name").asText();
                    }
                    
                    JobListing job = JobListing.builder()
                        .id(jobNode.path("id").asText())
                        .title(jobNode.path("title").asText())
                        .companyName(companyName)
                        .companyLinkedInUrl(linkedInUrl)
                        .location(jobNode.path("location").asText())
                        .description(jobNode.path("description").asText())
                        .jobType(jobType)
                        .url(jobNode.path("url").asText())
                        .build();
                    jobs.add(job);
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
    public List<JobListing> fetchJobsByLocation(String location) {
        String url = DEFAULT_API_BASE_URL + "/positions.json?location=" + location;
        ApiResponse response = get(url);
        
        if (response.isSuccess()) {
            try {
                JsonNode jsonNode = objectMapper.readTree(response.getData().toString());
                List<JobListing> jobs = new ArrayList<>();
                
                JsonNode resultsArray = jsonNode.isArray() ? jsonNode : jsonNode.get("results");
                if (resultsArray == null) {
                    resultsArray = jsonNode;
                }
                
                for (JsonNode jobNode : resultsArray) {
                    // Extract company information
                    JsonNode companyNode = jobNode.path("company");
                    String companyName = companyNode.path("name").asText();
                    String linkedInUrl = companyNode.path("linkedin_url").asText();
                    
                    // Extract job type from types array
                    String jobType = "";
                    JsonNode typesNode = jobNode.path("types");
                    if (typesNode.isArray() && typesNode.size() > 0) {
                        jobType = typesNode.get(0).path("name").asText();
                    }
                    
                    JobListing job = JobListing.builder()
                        .id(jobNode.path("id").asText())
                        .title(jobNode.path("title").asText())
                        .companyName(companyName)
                        .companyLinkedInUrl(linkedInUrl)
                        .location(jobNode.path("location").asText())
                        .description(jobNode.path("description").asText())
                        .jobType(jobType)
                        .url(jobNode.path("url").asText())
                        .build();
                    jobs.add(job);
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
    public List<JobListing> fetchJobsByCompany(String company) {
        String url = DEFAULT_API_BASE_URL + "/positions.json?company=" + company;
        ApiResponse response = get(url);
        
        if (response.isSuccess()) {
            try {
                JsonNode jsonNode = objectMapper.readTree(response.getData().toString());
                List<JobListing> jobs = new ArrayList<>();
                
                JsonNode resultsArray = jsonNode.isArray() ? jsonNode : jsonNode.get("results");
                if (resultsArray == null) {
                    resultsArray = jsonNode;
                }
                
                for (JsonNode jobNode : resultsArray) {
                    // Extract company information from nested object
                    String companyName = "";
                    String companyLinkedInUrl = "";
                    JsonNode companyNode = jobNode.path("company");
                    if (companyNode.isObject()) {
                        companyName = companyNode.path("name").asText("");
                        companyLinkedInUrl = companyNode.path("linkedInUrl").asText("");
                    } else {
                        companyName = companyNode.asText("");
                    }

                    // Extract job type from types array (get first type)
                    String jobType = "";
                    JsonNode typesArray = jobNode.path("types");
                    if (typesArray.isArray() && typesArray.size() > 0) {
                        jobType = typesArray.get(0).path("name").asText("");
                    } else {
                        jobType = jobNode.path("type").asText("");
                    }

                    JobListing job = JobListing.builder()
                        .id(jobNode.path("id").asText())
                        .title(jobNode.path("title").asText())
                        .companyName(companyName)
                        .companyLinkedInUrl(companyLinkedInUrl)
                        .location(jobNode.path("location").asText())
                        .description(jobNode.path("description").asText())
                        .jobType(jobType)
                        .url(jobNode.path("url").asText())
                        .build();
                    jobs.add(job);
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
}