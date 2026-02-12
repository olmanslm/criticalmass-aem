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
     *
     * @return API response with status and payload
     */
    ApiResponse get(String url);

    /**
     * Makes a POST request with JSON payload.
     *
     * @return API response with status and payload
     */
    ApiResponse post(String url, String jsonPayload);

    /**
     * Fetches job listings from external API.
     *
     * @return list of job listings
     */
    List<JobListing> fetchJobs();

    /**
     * Fetches job listings by location.
     *
     * @return list of job listings
     */
    List<JobListing> fetchJobsByLocation(String location);

    /**
     * Fetches job listings by company.
     *
     * @return list of job listings
     */
    List<JobListing> fetchJobsByCompany(String company);
}
