package com.criticalmass.core.servlets;

import com.criticalmass.core.services.SentimentService;
import com.criticalmass.core.services.dto.SentimentResult;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

@Component(
    service = Servlet.class,
    property = {
        "sling.servlet.paths=/bin/criticalmass/sentiment",
        "sling.servlet.methods=GET"
    }
)
public class SentimentServlet extends SlingSafeMethodsServlet {

    private static final int MAX_INPUT_SAMPLE_LENGTH = 80;
    private static final int MAX_INPUT_SAMPLE_SUBSTRING = 77;

    @Reference
    private SentimentService sentimentService;

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
            throws ServletException, IOException {

        String text = request.getParameter("text");
        SentimentResult r = sentimentService.analyze(text);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // very small manual JSON (safe enough for this demo)
        String safeText = text == null ? "" : text.replace("\\", "\\\\").replace("\"", "\\\"");
        response.getWriter().write(
            "{"
                + "\"inputSample\":\"" + (safeText.length() > MAX_INPUT_SAMPLE_LENGTH ? safeText.substring(0, MAX_INPUT_SAMPLE_SUBSTRING) + "..." : safeText) + "\","
                + "\"score\":" + String.format("%.3f", r.getScore()) + ","
                + "\"label\":\"" + r.getLabel() + "\","
                + "\"emoji\":\"" + r.getEmoji() + "\","
                + "\"positiveCount\":" + r.getPositiveCount() + ","
                + "\"negativeCount\":" + r.getNegativeCount() + ","
                + "\"tokens\":" + r.getTokens()
            + "}"
        );
    }
}
