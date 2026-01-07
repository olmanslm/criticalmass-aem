package com.criticalmass.core.services.impl;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(
    name = "Critical Mass - Job API Configuration",
    description = "Configuration for external job API service"
)
public @interface JobApiConfiguration {
    
    @AttributeDefinition(
        name = "Job API Base URL",
        description = "Base URL for the external job API",
        type = AttributeType.STRING
    )
    String apiBaseUrl() default "https://jobs.github.com";
    
    @AttributeDefinition(
        name = "Connection Timeout",
        description = "Connection timeout in milliseconds",
        type = AttributeType.INTEGER
    )
    int connectionTimeout() default 5000;
    
    @AttributeDefinition(
        name = "Read Timeout",
        description = "Read timeout in milliseconds",
        type = AttributeType.INTEGER
    )
    int readTimeout() default 10000;
    
    @AttributeDefinition(
        name = "Enable Caching",
        description = "Enable response caching",
        type = AttributeType.BOOLEAN
    )
    boolean enableCaching() default true;
    
    @AttributeDefinition(
        name = "Cache Duration",
        description = "Cache duration in minutes",
        type = AttributeType.INTEGER
    )
    int cacheDuration() default 15;
}