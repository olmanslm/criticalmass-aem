/*
 *  Copyright 2015 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.criticalmass.core.models;

import static org.apache.sling.api.resource.ResourceResolver.PROPERTY_RESOURCE_TYPE;

import javax.annotation.PostConstruct;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import com.criticalmass.core.services.GreetingService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Model(
    adaptables = Resource.class,
    adapters = HelloWorldModel.class,
    resourceType = "criticalmass/components/helloworld"
)
public class HelloWorldModel {

    @OSGiService
    private GreetingService greetingService;
    @ValueMapValue(name = PROPERTY_RESOURCE_TYPE, injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "No resourceType")
    protected String resourceType;

    @SlingObject
    private Resource currentResource;
    @SlingObject
    private ResourceResolver resourceResolver;

    private String message;

    @ValueMapValue(name = "name", injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "Default from HelloWorldModel")
    private String name;

    @ValueMapValue(name = "number", injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "999")
    private int number;

    @PostConstruct
    protected void init() {
        String currentPagePath = currentResource != null ? currentResource.getPath() : "";

        message = "Hello World!\n"
            + "Resource type is: " + resourceType + "\n"
            + "Current resource path is:  " + currentPagePath + "\n";

        // Example: call GreetingService with dialog values
        if (greetingService != null) {
            String greeting = greetingService.getMessage(name, number);
            message += "GreetingService: " + greeting + "\n";
        }

    log.debug("Initialized HelloWorldModel with message: {}", message);
    }

    public String getMessage() {
        return message;
    }

    // Expose direct call for HTL
    /**
     * Returns a greeting message from the GreetingService using the current name and number.
     *
     * @return the greeting message, or an empty string if the service is unavailable
     */
    public String getGreetingService() {
        if (greetingService != null) {
            return greetingService.getMessage(name, number);
        }
        return "";
    }

}
