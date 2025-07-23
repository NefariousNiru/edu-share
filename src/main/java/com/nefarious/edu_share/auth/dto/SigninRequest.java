package com.nefarious.edu_share.auth.dto;

import com.nefarious.edu_share.auth.util.Constants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** This class is used to capture the data required for user login. Fields:Email & Password */
@Data
public class SigninRequest {
    @Email
    @NotBlank
    private String email;
    @NotBlank @Size(min = 8, max = 24)
    @Pattern(regexp = Constants.VALID_PASSWORD_REGEXP,
            message = Constants.INVALID_PASSWORD_COMBINATION)
    @NotBlank @Size(min = 8, max = 24)
    private String password;
}
