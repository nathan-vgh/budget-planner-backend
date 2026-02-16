package com.budget_planner.budget_planner.common.exceptions;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import tools.jackson.databind.exc.InvalidFormatException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors (MethodArgumentNotValidException exception) {
        var fieldErrors = new HashMap<String, String>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error -> fieldErrors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.badRequest().body(
                Map.of(
                        "status", 400,
                        "error", "Validation fields",
                        "fields", fieldErrors
                )
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleInvalidJson (HttpMessageNotReadableException exception) {
        if (exception.getCause() instanceof InvalidFormatException invalidFormatException && invalidFormatException.getTargetType().isEnum()) {
            var enumValues = invalidFormatException.getTargetType().getEnumConstants();

            var allowedValues = Arrays.stream(enumValues)
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));

            return ResponseEntity.badRequest().body(new ApiError("Invalid value. Allowed values: " + allowedValues));
        }

        return ResponseEntity.badRequest().body(new ApiError("Malformed Json request"));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFoundException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiError(exception.getMessage(), 404));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handlerMethodArgumentTypeMismatch (MethodArgumentTypeMismatchException exception) {
        var parameter = exception.getName();
        var value = exception.getValue();
        var expectedType = exception.getRequiredType();

        String message;

        if (expectedType != null) {
            message = String.format(
                    "Invalid value '%s' for parameter '%s'. Expected type: %s",
                    value,
                    parameter,
                    expectedType.getSimpleName()
            );
        } else {
            message = String.format(
                    "Invalid value '%s' for parameter '%s'",
                    value,
                    parameter
            );
        }

        return ResponseEntity.badRequest().body(new ApiError(message));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolation (DataIntegrityViolationException exception) {
        var constraint = extractConstraintName(exception);
        var message = getConstraintMessage(constraint);
        return ResponseEntity.badRequest().body(new ApiError(message));
    }

    private String extractConstraintName (Throwable exception) {
        var cause = exception;

        // Loop over the error stack and find the constraint violated.
        while (cause != null) {
            if (cause instanceof ConstraintViolationException constraint) {
                return constraint.getConstraintName();
            }

            cause = cause.getCause();
        }

        return null;
    }

    private String getConstraintMessage (String constraint) {
        if (constraint == null)
            return "Data integrity violation";

        return switch (constraint) {
            case "uk_user_email" -> "Email already exists";
            case "uk_user_settings" -> "User has an existing user settings record";
            case "uk_user_tag" -> "User has an existing tag with that name";
            case "uk_user_category" -> "User has an existing category with that name";
            case "uk_user_account" -> "User has an existing account with that name";
            default -> "Data integrity violation";
        };
    }
}
