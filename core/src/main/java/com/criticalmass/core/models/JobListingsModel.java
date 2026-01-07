package com.criticalmass.core.models;

import com.criticalmass.core.services.JobApiService;
import com.criticalmass.core.services.dto.JobListing;
import lombok.Getter;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Model for displaying job listings from external API.
 */
@Model(
    adaptables = SlingHttpServletRequest.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class JobListingsModel {
    
    @Getter
    @ValueMapValue(name = "location")
    private String location;
    
    @Getter
    @ValueMapValue(name = "company")
    private String company;
    
    @Getter
    @ValueMapValue(name = "maxJobs")
    @Default(intValues = 10)
    private int maxJobs;
    
    @OSGiService
    private JobApiService jobApiService;
    
    private List<JobListing> jobsList;
    
    @PostConstruct
    protected void init() {
        if (jobApiService != null) {
            if (location != null && !location.trim().isEmpty()) {
                // Fetch jobs by location
                jobsList = jobApiService.fetchJobsByLocation(location);
            } else if (company != null && !company.trim().isEmpty()) {
                // Fetch jobs by company
                jobsList = jobApiService.fetchJobsByCompany(company);
            } else {
                // Fetch all jobs
                jobsList = jobApiService.fetchJobs();
            }
            
            // Limit results
            if (jobsList != null && jobsList.size() > maxJobs) {
                jobsList = jobsList.subList(0, maxJobs);
            }
        }
    }
    
    // Getters for HTL
    public List<JobListing> getJobs() {
        return jobsList != null ? jobsList : new ArrayList<>();
    }
    
    public boolean hasJobs() {
        return jobsList != null && !jobsList.isEmpty();
    }
    
    public int getJobCount() {
        return jobsList != null ? jobsList.size() : 0;
    }
    
    public boolean hasApiError() {
        return jobApiService == null || (jobsList == null || jobsList.isEmpty());
    }
    
    public String getSearchType() {
        if (location != null && !location.trim().isEmpty()) {
            return "location";
        } else if (company != null && !company.trim().isEmpty()) {
            return "company";
        }
        return "all";
    }
    
    public String getSearchValue() {
        if (location != null && !location.trim().isEmpty()) {
            return location;
        } else if (company != null && !company.trim().isEmpty()) {
            return company;
        }
        return "all jobs";
    }
}