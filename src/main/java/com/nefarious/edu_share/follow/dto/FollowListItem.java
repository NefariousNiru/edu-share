package com.nefarious.edu_share.follow.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FollowListItem {
    private String username;
    private String profileImageUrl;
    private String affiliation; // Either Organization
}
