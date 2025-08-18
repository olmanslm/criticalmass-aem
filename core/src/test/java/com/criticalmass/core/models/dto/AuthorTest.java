package com.criticalmass.core.models.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test DTO created with Lombok annotations.
 */
class AuthorTest {

    @Test
    void testAuthorLombokGeneratedMethods() {
        Author author = new Author("Olman", "olman@example.com", 10);

        assertEquals("Olman", author.getName());
        assertEquals("olman@example.com", author.getEmail());
        assertEquals(10, author.getArticles());

        // Lombok generates toString
        assertTrue(author.toString().contains("Olman"));
    }
}