package com.criticalmass.core.servlets;

import com.criticalmass.core.services.JobApiService;
import com.criticalmass.core.services.dto.JobListing;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.List;

@Component(
    service = Servlet.class,
    property = {
        "sling.servlet.paths=/bin/criticalmass/job-listings",
        "sling.servlet.methods=GET"
    }
)
public class JobListingsServlet extends SlingSafeMethodsServlet {

    @Reference
    private JobApiService jobApiService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String location = request.getParameter("location");
        String company = request.getParameter("company");

        try {
            List<JobListing> jobs;

            if (location != null && !location.trim().isEmpty()) {
                // Return jobs filtered by location
                jobs = jobApiService.fetchJobsByLocation(location);
            } else if (company != null && !company.trim().isEmpty()) {
                // Return jobs filtered by company
                jobs = jobApiService.fetchJobsByCompany(company);
            } else {
                // Return all jobs
                jobs = jobApiService.fetchJobs();
            }

            String json = objectMapper.writeValueAsString(jobs);
            response.getWriter().write(json);

        } catch (Exception e) {
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Internal server error: " + e.getMessage() + "\"}");
        }
    }
}
