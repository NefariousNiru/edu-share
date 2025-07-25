package com.nefarious.edu_share.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PublicUserProfile {
    private String    username;
    private String    firstName;
    private String    lastName;
    private String    bio;
    private String    profileImageUrl;
    private String    organization;
    private String    school;
    private String    position;
}
