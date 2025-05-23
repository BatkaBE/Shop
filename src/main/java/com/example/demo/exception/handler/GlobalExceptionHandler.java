package com.example.demo.exception.handler;// GlobalExceptionHandler.java

import com.example.demo.exception.response.ErrorResponse;
import com.example.demo.exception.error.AppException;
import com.example.demo.exception.error.NotFoundError;
import com.example.demo.exception.error.UniqueFieldError;
import com.example.demo.exception.error.ValidationError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundError.class)
    public ResponseEntity<ErrorResponse> handleNotFoundError(NotFoundError ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ValidationError.class)
    public ResponseEntity<ErrorResponse> handleValidationError(ValidationError ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UniqueFieldError.class)
    public ResponseEntity<ErrorResponse> handleUniqueFieldError(UniqueFieldError ex) {
        Map<String, Object> details = new HashMap<>();
        details.put("field", ex.getField());
        details.put("value", ex.getValue());
        ErrorResponse error = new ErrorResponse(ex.getMessage(), details);
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleAppException(AppException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse error = new ErrorResponse("An unexpected error occurred: " + ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}