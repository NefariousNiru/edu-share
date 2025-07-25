package com.nefarious.edu_share.user.controller;

import com.nefarious.edu_share.user.dto.PublicUserProfile;
import com.nefarious.edu_share.user.dto.UpdateUserProfileRequest;
import com.nefarious.edu_share.user.dto.UserProfile;
import com.nefarious.edu_share.user.service.UserProfileService;
import com.nefarious.edu_share.user.service.UserService;
import com.nefarious.edu_share.user.util.UserEndpoint;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping(UserEndpoint.USER)
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserProfileService userProfileService;

    /**
     * Gets a user's own profile
     * @param userId Principal account holder userID
     * @return UserProfile of user
     */
    @GetMapping(UserEndpoint.ME)
    public Mono<UserProfile> getMyProfile(@AuthenticationPrincipal UUID userId) {
        return userProfileService.getMyProfile(userId);
    }

    /**
     * Updates a user's own profile
     * @param userId Principal account holder userID
     * @param dto Updates to be applied
     * @return Updated UserProfile of user
     */
    @PatchMapping(UserEndpoint.ME)
    public Mono<UserProfile> updateMyProfile(@AuthenticationPrincipal UUID userId, @Valid @RequestBody UpdateUserProfileRequest dto) {
        return userProfileService.updateProfile(userId, dto);
    }

    /**
     * Gets any publicly available profile
     * @param username username of the requested profile
     * @return PublicUserProfile of requested user
     */
    @GetMapping(UserEndpoint.PROFILE)
    public Mono<PublicUserProfile> getProfile(@PathVariable String username) {
        return userProfileService.getPublicProfile(username);
    }
}
