package com.criticalmass.core.services;

import com.criticalmass.core.services.dto.ApiResponse;
import com.criticalmass.core.services.dto.JobListing;

import java.util.List;

/**
 * Service for making HTTP requests to job APIs.
 */
public interface JobApiService {
    
    /**
     * Makes a GET request to the specified URL.
     */
    ApiResponse get(String url);
    
    /**
     * Makes a POST request with JSON payload.
     */
    ApiResponse post(String url, String jsonPayload);
    
    /**
     * Fetches job listings from external API.
     */
    List<JobListing> fetchJobs();
    
    /**
     * Fetches job listings by location.
     */
    List<JobListing> fetchJobsByLocation(String location);
    
    /**
     * Fetches job listings by company.
     */
    List<JobListing> fetchJobsByCompany(String company);
}