package com.criticalmass.core.services.dto;

import lombok.Builder;
import lombok.Value;

/**
 * API response data structure for external service integration.
 */
@Value
@Builder(toBuilder = true)
public class ApiResponse {
    boolean success;
    String message;
    Object data;
    int statusCode;
    String errorMessage;
}