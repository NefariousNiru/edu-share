package com.nefarious.edu_share.auth.dto;

import com.nefarious.edu_share.auth.util.Constants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** This class is used to capture email and otp. It also performs certain validations.*/
@Data
public class OtpVerificationRequest {
    @Email
    @NotBlank
    private String email;
    @NotBlank @Size(min=10, max=10) @Pattern(regexp = Constants.VALID_OTP_REGEXP, message = Constants.INVALID_OTP) private String code;
}
