package com.criticalmass.core.services.impl;

import com.criticalmass.core.services.GreetingService;
import org.osgi.service.component.annotations.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * OSGi service implementation.
 * - @Component registers this as an OSGi service.
 * - The 'service' attribute tells OSGi which interface it implements.
 * - @Slf4j is a Lombok annotation that gives us a 'log' instance without writing boilerplate.
 */
@Component(service = GreetingService.class)
@Slf4j
public class GreetingServiceImpl implements GreetingService {

    @Override
    public String getMessage(final String name, final int number) {
        log.debug("Processing started for name: {} and number: {}", name, number);
        log.info("Business logic running");
        log.error("Something went wrong!");
        return String.format("Hello %s! Your number is %d.", name, number);
    }
}
