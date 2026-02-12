package com.criticalmass.core.services.impl;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(
    name = "Critical Mass - Job API Configuration",
    description = "Configuration for external job API service"
)
public @interface JobApiConfiguration {

    /**
     * Base URL for the external job API.
     *
     * @return API base URL
     */
    @AttributeDefinition(
        name = "Job API Base URL",
        description = "Base URL for the external job API",
        type = AttributeType.STRING
    )
    String apiBaseUrl() default "https://jobdataapi.com/api/jobs/";

    /**
     * Connection timeout in milliseconds.
     *
     * @return connection timeout
     */
    @AttributeDefinition(
        name = "Connection Timeout",
        description = "Connection timeout in milliseconds",
        type = AttributeType.INTEGER
    )
    int connectionTimeout() default 5000;

    /**
     * Read timeout in milliseconds.
     *
     * @return read timeout
     */
    @AttributeDefinition(
        name = "Read Timeout",
        description = "Read timeout in milliseconds",
        type = AttributeType.INTEGER
    )
    int readTimeout() default 10000;

    /**
     * Enables or disables response caching.
     *
     * @return true when caching is enabled
     */
    @AttributeDefinition(
        name = "Enable Caching",
        description = "Enable response caching",
        type = AttributeType.BOOLEAN
    )
    boolean enableCaching() default true;

    /**
     * Cache duration in minutes.
     *
     * @return cache duration
     */
    @AttributeDefinition(
        name = "Cache Duration",
        description = "Cache duration in minutes",
        type = AttributeType.INTEGER
    )
    int cacheDuration() default 15;
}
