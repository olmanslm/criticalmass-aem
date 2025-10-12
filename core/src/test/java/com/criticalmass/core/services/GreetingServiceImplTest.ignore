package com.criticalmass.core.services;

import com.criticalmass.core.services.impl.GreetingServiceImpl;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple unit test for OSGi service.
 */
class GreetingServiceImplTest {

    private static final int MAGIC_INTEGER = 42;

    @Test
    void testGetMessage() {
        GreetingService service = new GreetingServiceImpl();
        String message = service.getMessage("description", MAGIC_INTEGER);

        assertNotNull(message, "Message should not be null");
        assertEquals("Hello from AEM Service!", message);
    }
}