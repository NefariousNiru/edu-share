package com.nefarious.edu_share.shared.exceptions;

import com.nefarious.edu_share.shared.interfaces.BusinessError;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum BaseError implements BusinessError {
    TOO_MANY_ATTEMPTS           ("You went too fast and hit a wall. Try later!", HttpStatus.TOO_MANY_REQUESTS),
    VALIDATION_FAILED           ("Validation failed",           HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR       ("Oh we ran into a hiccup!",    HttpStatus.INTERNAL_SERVER_ERROR),
    EMPTY_MESSAGE               ("Your request is empty!",      HttpStatus.BAD_REQUEST)
    ;
    private final String message;
    private final HttpStatus httpStatus;
}
