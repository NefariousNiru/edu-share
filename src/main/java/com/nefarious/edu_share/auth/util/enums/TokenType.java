package com.nefarious.edu_share.auth.util.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Types of Tokens
 * <br>
 * ACCESS
 * <br>
 * REFRESH
 */
@AllArgsConstructor
@Getter
public enum TokenType {
    ACCESS("access"), REFRESH("refresh");
    private final String value;
}
