package com.criticalmass.core.models;

import com.criticalmass.core.services.JobApiService;
import com.criticalmass.core.services.dto.JobListing;
import lombok.Getter;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Model for displaying job listings from external API.
 */
@Model(
    adaptables = org.apache.sling.api.SlingHttpServletRequest.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class JobListingsModel {
    private static final Logger LOG = LoggerFactory.getLogger(JobListingsModel.class);

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
    private boolean apiError;

    @PostConstruct
    protected void init() {
        LOG.info("JobListingsModel init - location='{}', company='{}', maxJobs={}",
            location, company, maxJobs);

        if (jobApiService == null) {
            jobsList = new ArrayList<>();
            apiError = true;
            LOG.warn("JobListingsModel - JobApiService is not available");
            return;
        }

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

        if (jobsList == null) {
            jobsList = new ArrayList<>();
            apiError = true;
            LOG.warn("JobListingsModel - null response received from API");
            return;
        }

        int totalResults = jobsList.size();

        // Limit results
        if (jobsList.size() > maxJobs) {
            jobsList = jobsList.subList(0, maxJobs);
        }

        LOG.info("JobListingsModel results - total={}, rendered={}, filter='{}', value='{}'",
            totalResults, jobsList.size(), getSearchType(), getSearchValue());
    }

    // Getters for HTL
    /**
     * Returns the resolved job list, or an empty list when no data is available.
     *
     * @return list of job listings
     */
    public List<JobListing> getJobs() {
        return jobsList != null ? jobsList : new ArrayList<>();
    }

    /**
     * Indicates whether any jobs are available.
     *
     * @return true when the list contains jobs
     */
    public boolean hasJobs() {
        return jobsList != null && !jobsList.isEmpty();
    }

    /**
     * Returns the number of jobs currently loaded.
     *
     * @return job count
     */
    public int getJobCount() {
        return jobsList != null ? jobsList.size() : 0;
    }

    /**
     * Indicates a possible API error when no service or results are available.
     *
     * @return true when the API call failed or returned no results
     */
    public boolean hasApiError() {
        return apiError;
    }

    /**
     * Returns the active search filter type.
     *
     * @return location, company, or all
     */
    public String getSearchType() {
        if (location != null && !location.trim().isEmpty()) {
            return "location";
        } else if (company != null && !company.trim().isEmpty()) {
            return "company";
        }
        return "all";
    }

    /**
     * Returns the search filter value.
     *
     * @return search value used for filtering
     */
    public String getSearchValue() {
        if (location != null && !location.trim().isEmpty()) {
            return location;
        } else if (company != null && !company.trim().isEmpty()) {
            return company;
        }
        return "all jobs";
    }
}
