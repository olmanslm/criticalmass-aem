package com.criticalmass.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import io.wcm.testing.mock.aem.junit5.AemContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.criticalmass.core.services.GreetingService;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit test for Sling Servlet.
 */
class HelloServletTest {

    private final AemContext context = new AemContext();
    private static final int MAGIC_INTEGER = 422;

    @BeforeEach
    void setUp() {
        // Mock OSGi service
        GreetingService mockService = mock(GreetingService.class);
        when(mockService.getMessage("description", MAGIC_INTEGER)).thenReturn("Mocked Greeting!");

        context.registerService(GreetingService.class, mockService);

        // Create parent resource
        context.create().resource("/content/article",
            "title", "Test Article",
            "description", "This is a test article"
        );

        // Create child resource separately
        context.create().resource("/content/article/author",
            "name", "Olman",
            "email", "olman@example.com",
            "articles", 5
        );
    }

    @Test
    void testDoGet() throws Exception {
        HelloServlet servlet = new HelloServlet();

        // Mock request and response
        SlingHttpServletRequest request = mock(SlingHttpServletRequest.class);
        SlingHttpServletResponse response = mock(SlingHttpServletResponse.class);
        
        // Mock request path info and resource path
        org.apache.sling.api.request.RequestPathInfo pathInfo = mock(org.apache.sling.api.request.RequestPathInfo.class);
        when(request.getRequestPathInfo()).thenReturn(pathInfo);
        when(pathInfo.getResourcePath()).thenReturn("/bin/hello");

        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);
        when(response.getWriter()).thenReturn(writer);

        servlet.doGet(request, response);

        writer.flush();
        assertTrue(sw.toString().contains("Hello from Sling Servlet with Lombok!"));
    }
}