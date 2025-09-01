package com.criticalmass.core.models;

import com.criticalmass.core.models.dto.Author;
import com.criticalmass.core.services.GreetingService;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import javax.annotation.PostConstruct;
import org.apache.sling.api.resource.Resource;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Sling Model for a resource-based component.
 * - @Model: Defines this class as a Sling Model.
 * - adaptables = Resource: It can be adapted from a JCR node.
 * - adapters = Article.class: It exposes itself via an interface (optional).
 * - @ValueMapValue: Injects properties from the JCR node.
 * - @ChildResource: Injects a nested resource (e.g., 'author' node).
 * - @OSGiService: Injects an OSGi service (like GreetingService).
 * - @Getter (Lombok): Auto-generates getters for fields.
 * - @Slf4j (Lombok): Auto-generates a logger.
 */
@Getter
@Slf4j
@Model(adaptables = Resource.class)
public class ArticleModel {

    @ValueMapValue
    private String title;

    @ValueMapValue
    private String description;

    @ChildResource
    private Resource author;

    @OSGiService
    private GreetingService greetingService;

    private Author authorDto;

    private String greetingMessage;

    @PostConstruct
    protected void init() {
        log.debug("Initializing ArticleModel for title={}", title);

        // Build Author DTO from child resource
        if (author != null) {
            this.authorDto = new Author(
                author.getValueMap().get("name", String.class),
                author.getValueMap().get("email", String.class),
                author.getValueMap().get("articles", 0)
            );
        }

        // Call OSGi service
        this.greetingMessage = greetingService.getMessage("description", 420);
    }
}
