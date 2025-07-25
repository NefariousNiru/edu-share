package com.nefarious.edu_share.shared.exceptions;

import com.nefarious.edu_share.shared.dto.BusinessErrorResponse;
import com.nefarious.edu_share.shared.interfaces.BusinessError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebInputException;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /** Handler for your custom business exceptions */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<BusinessErrorResponse> handleBusiness(BusinessException ex) {
        log.error(Arrays.toString(ex.getStackTrace()));
        BusinessError code = ex.getBusinessError();
        BusinessErrorResponse body = new BusinessErrorResponse(code, code.getMessage(), System.currentTimeMillis());
        return ResponseEntity
                .status(code.getHttpStatus())
                .body(body);
    }

    /** Handle @Valid bean validation failures */
    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<BusinessErrorResponse> handleWebExchangeBindException(WebExchangeBindException ex) {
        log.error(Arrays.toString(ex.getStackTrace()));
        String errors = ex.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        BusinessErrorResponse body = new BusinessErrorResponse(BaseError.VALIDATION_FAILED, errors, System.currentTimeMillis());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(body);
    }

    /** Handle when request body is empty failures */
    @ExceptionHandler(ServerWebInputException.class)
    public ResponseEntity<BusinessErrorResponse> handleMissingBody(ServerWebInputException ex) {
        BusinessErrorResponse body = new BusinessErrorResponse(BaseError.EMPTY_MESSAGE, BaseError.EMPTY_MESSAGE.getMessage(), System.currentTimeMillis());
        return ResponseEntity.badRequest().body(body);
    }

    /** Fallback for any other unhandled exception */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BusinessErrorResponse> handleAll(Exception ex) {
        log.error(Arrays.toString(ex.getStackTrace()));
        log.error(ex.getMessage());
        HttpStatusCode status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = BaseError.INTERNAL_SERVER_ERROR.getMessage();
        BaseError error = BaseError.INTERNAL_SERVER_ERROR;

        if (ex instanceof ResponseStatusException rse) {
            status = rse.getStatusCode();
            message = rse.getReason() != null
                    ? rse.getReason()
                    : status.value() + " " + status;

            if (status.value() == HttpStatus.NOT_FOUND.value()) {
                error = BaseError.NOT_FOUND;
            } else if (status.value() == HttpStatus.BAD_REQUEST.value()) {
                error = BaseError.VALIDATION_FAILED;
            }
        }

        BusinessErrorResponse body = new BusinessErrorResponse(
                error, message, System.currentTimeMillis()
        );

        return ResponseEntity.status(status).body(body);
    }
}
