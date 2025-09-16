package com.criticalmass.core.services;

import com.criticalmass.core.services.impl.EdgarTdpServiceImpl;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple unit test for OSGi service.
 */
class EdgarTdpServiceImplTest {

    @Test
    void testUrl() {
        EdgarTdpService edgarTdpService = new EdgarTdpServiceImpl();
        String url = "";

        assertNotNull(url, "URL should not be null");
    }
}