package com.nefarious.edu_share.auth.dto;

import com.nefarious.edu_share.auth.util.Constants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** Request body class for a forgot password verify request. Inherites OtpVerificationRequest as password can be updated when Otp is verified*/
@EqualsAndHashCode(callSuper = true)
@Data
public class ForgotPasswordRequest extends OtpVerificationRequest {
    @NotBlank
    @Size(min = 8, max = 24)
    @Pattern(regexp = Constants.VALID_PASSWORD_REGEXP,
            message = Constants.INVALID_PASSWORD_COMBINATION)
    private String newPassword;
}
