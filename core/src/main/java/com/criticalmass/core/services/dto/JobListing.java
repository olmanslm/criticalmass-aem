package com.criticalmass.core.services.dto;

import lombok.Builder;
import lombok.Value;

/**
 * Job listing data from external API.
 */
@Value
@Builder(toBuilder = true)
public class JobListing {
    String id;
    String title;
    String companyName;
    String companyLinkedInUrl;
    String location;
    String description;
    String jobType;
    String url;
}
