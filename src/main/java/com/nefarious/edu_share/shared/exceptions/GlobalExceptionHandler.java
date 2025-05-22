package com.nefarious.edu_share.shared.exceptions;

import com.nefarious.edu_share.shared.dto.BusinessErrorResponse;
import com.nefarious.edu_share.shared.interfaces.BusinessError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    /** Handler for your custom business exceptions */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<BusinessErrorResponse> handleBusiness(BusinessException ex) {
        BusinessError code = ex.getBusinessError();
        BusinessErrorResponse body = new BusinessErrorResponse(code, code.getMessage(), System.currentTimeMillis());
        return ResponseEntity
                .status(code.getHttpStatus())
                .body(body);
    }

    /** Handle @Valid bean validation failures */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BusinessErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        BusinessErrorResponse body = new BusinessErrorResponse(BaseError.VALIDATION_FAILED, errors, System.currentTimeMillis());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(body);
    }

    /** Fallback for any other unhandled exception */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BusinessErrorResponse> handleAll(Exception ex) {
        BusinessErrorResponse body = new BusinessErrorResponse(
                BaseError.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred",
                System.currentTimeMillis()
        );
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(body);
    }
}
