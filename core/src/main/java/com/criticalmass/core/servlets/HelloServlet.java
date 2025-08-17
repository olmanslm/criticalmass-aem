package com.criticalmass.core.servlets;

import org.osgi.service.component.annotations.Component;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import lombok.extern.slf4j.Slf4j;

/**
 * Sling Servlet example.
 * - @Component: Registers servlet as OSGi service.
 * - service = Servlet.class: Declares it as a servlet.
 * - sling.servlet.* properties: Define how it's mapped (path, method, resourceType).
 * - @Slf4j (Lombok): Adds logging.
 */
@Component(service = Servlet.class,
           property = {
               "sling.servlet.methods=GET",
               "sling.servlet.paths=/bin/hello"
           })
@Slf4j
public class HelloServlet extends SlingSafeMethodsServlet {

    @Override
    protected void doGet(final SlingHttpServletRequest req, final SlingHttpServletResponse resp)
        throws ServletException, IOException {

        log.info("HelloServlet called at {}", req.getRequestPathInfo().getResourcePath());
        resp.getWriter().write("Hello from Sling Servlet with Lombok!");
    }
}
