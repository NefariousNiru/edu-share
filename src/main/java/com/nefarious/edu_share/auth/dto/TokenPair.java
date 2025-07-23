package com.nefarious.edu_share.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** This class represents token pairs. Access and Refresh*/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenPair {
    @NotBlank
    @NotNull
    private String accessToken;
    @NotBlank
    @NotNull
    private String refreshToken;
}
