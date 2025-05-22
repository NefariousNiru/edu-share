package com.nefarious.edu_share.shared.exceptions;

import lombok.Getter;
import com.nefarious.edu_share.shared.interfaces.BusinessError;

/**
 * Custom runtime exception to handle business logic errors across the application.
 *
 * <p>Encapsulates an {@link BusinessError} containing the error message and HTTP status,
 * allowing consistent exception handling across different modules.
 */
@Getter
public class BusinessException extends RuntimeException {
    private final BusinessError businessError;
    public BusinessException(BusinessError businessError) {
        super(businessError.getMessage());
        this.businessError = businessError;
    }
}