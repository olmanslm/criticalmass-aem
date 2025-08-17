package com.criticalmass.core.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * POJO (Plain Old Java Object) used as a DTO.
 * - @Data (Lombok) generates getters, setters, equals, hashCode, and toString.
 * - @AllArgsConstructor generates a constructor with all fields.
 * This avoids boilerplate and makes DTOs super clean.
 */
@Data
@AllArgsConstructor
public class Author {
    private String name;
    private String email;
    private int articles;
}
