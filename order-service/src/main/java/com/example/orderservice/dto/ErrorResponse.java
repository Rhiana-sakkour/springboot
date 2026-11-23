package com.example.orderservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/** Error body. "details" is omitted from the JSON when it is null or empty. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ErrorResponse(String message, List<String> details) {

    public ErrorResponse(String message) {
        this(message, null);
    }
}
