package com.nefarious.edu_share.auth.dto;

import com.nefarious.edu_share.auth.util.Constants;
import com.nefarious.edu_share.user.annotation.MinimumAge;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

/** This class is used to capture the data required for user registration, including validation
 * for the email, password, username, firstname, lastname, and date of birth fields */
@Data
public class SignupRequest {
    @Email
    @NotBlank
    private String email;
    @NotBlank @Size(min = 8, max = 24)
    @Pattern(regexp = Constants.VALID_PASSWORD_REGEXP,
            message = Constants.INVALID_PASSWORD_COMBINATION)
    private String password;
    @NotBlank @Size(min=1, max = 30) private String username;
    @NotBlank private String firstName;
    @NotBlank private String lastName;
    @NotNull @Past @MinimumAge(13) LocalDate dateOfBirth;
}
