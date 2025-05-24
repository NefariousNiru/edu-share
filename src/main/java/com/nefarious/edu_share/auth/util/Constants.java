package com.nefarious.edu_share.auth.util;

public class Constants {
    public static final String INVALID_PASSWORD_COMBINATION = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character.";
    public static final String VALID_PASSWORD_REGEXP = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9])[^\\s]{8,24}$";
    public static final String VALID_OTP_REGEXP = "^[A-Za-z0-9]{10}$";
    public static final String INVALID_OTP = "Invalid OTP";
}
