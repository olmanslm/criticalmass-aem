package com.criticalmass.core.models;

import com.criticalmass.core.models.dto.Author;
import com.criticalmass.core.services.GreetingService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Integration test for ArticleModel using AEM Mocks.
 */
class ArticleModelTest {

    private final AemContext context = new AemContext();

    @BeforeEach
    void setUp() {
        context.addModelsForPackage("com.criticalmass.core.models");
        GreetingService mockService = mock(GreetingService.class);
        when(mockService.getMessage()).thenReturn("Mocked Greeting!");
        context.registerService(GreetingService.class, mockService);
        context.create().resource("/content/article",
            "title", "Test Article",
            "description", "This is a test article"
        );
        context.create().resource("/content/article/author",
            "name", "Olman",
            "email", "olman@example.com",
            "articles", 5
        );
    }

    @Test
    void testArticleModel() {
        Resource resource = context.resourceResolver().getResource("/content/article");
        Resource authorResource = context.resourceResolver().getResource("/content/article/author");
        assertNotNull(resource, "Parent resource should not be null");
        assertNotNull(authorResource, "Child resource should not be null");
        Resource childNode = resource.getChild("author");
        assertNotNull(childNode, "Parent should have a child named 'author'");
        assertEquals(authorResource.getPath(), childNode.getPath(), "Child resource path should match");
        assertEquals(authorResource.getValueMap(), childNode.getValueMap(), "Child resource properties should match");

        ArticleModel model = resource.adaptTo(ArticleModel.class);
        assertNotNull(model, "ArticleModel adaptation should not return null");
        assertEquals("Test Article", model.getTitle());
        assertEquals("This is a test article", model.getDescription());

        Author author = model.getAuthorDto();
        assertNotNull(author, "Author DTO should not be null");
        assertEquals("Olman", author.getName());
        assertEquals("olman@example.com", author.getEmail());
        assertEquals(5, author.getArticles());

        assertEquals("Mocked Greeting!", model.getGreetingMessage());

        assertNotNull(resource.getChild("author"), "Parent should have a child named 'author'");
        assertEquals("/content/article/author", resource.getChild("author").getPath());
        assertEquals(authorResource.getValueMap(), resource.getChild("author").getValueMap());
    }
}