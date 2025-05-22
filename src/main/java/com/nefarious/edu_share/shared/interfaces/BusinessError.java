package com.nefarious.edu_share.shared.interfaces;

import org.springframework.http.HttpStatus;

/** Inherit with Error Enums only */
public interface BusinessError {
    /** Get Error Message corresponding to the ErrorCode */
    String getMessage();
    /** Get {@link HttpStatus} corresponding to the ErrorCode */
    HttpStatus getHttpStatus();
}
