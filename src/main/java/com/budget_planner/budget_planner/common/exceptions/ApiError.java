package com.budget_planner.budget_planner.common.exceptions;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ApiError (
        String message,
        int status,
        LocalDateTime timestamp
) {
    public ApiError (String message) {
        this (message, 400, LocalDateTime.now());
    }
    public ApiError (String message, int status) { this (message, status, LocalDateTime.now());}
}
