package com.nefarious.edu_share.user.dto;

import com.nefarious.edu_share.user.util.Constants;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class UpdateUserProfileRequest {
    @Size(max = 250, message = Constants.INVALID_BIO_SIZE) private String bio;
    @URL(message = Constants.INVALID_URL) private String profileImageUrl;
    @Size(max = 50, message = Constants.INVALID_ORG_NAME) private String organization;
    @Size(max = 50, message = Constants.INVALID_SCHOOL_NAME) private String school;
    @Size(max = 25, message = Constants.INVALID_POSITION) private String position;
}
