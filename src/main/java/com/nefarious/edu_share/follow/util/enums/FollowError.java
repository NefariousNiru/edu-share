package com.nefarious.edu_share.follow.util.enums;


import com.nefarious.edu_share.shared.interfaces.BusinessError;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum FollowError implements BusinessError {
    CANNOT_FOLLOW_SELF ("Cannot follow yourself", HttpStatus.FORBIDDEN)
    ;

    private final String message;
    private final HttpStatus httpStatus;
}