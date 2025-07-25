package com.nefarious.edu_share.follow.controller;


import com.nefarious.edu_share.follow.dto.FollowListItem;
import com.nefarious.edu_share.follow.service.FollowService;
import com.nefarious.edu_share.follow.util.FollowEndpoint;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping(FollowEndpoint.FOLLOW)
@RequiredArgsConstructor
public class FollowController {
    private final FollowService followService;

    /**
     * Follow the given username.
     */
    @PostMapping(FollowEndpoint.USERNAME)
    public Mono<Void> follow(@AuthenticationPrincipal UUID userId, @PathVariable String usernameToFollow) {
        return followService.follow(userId, usernameToFollow);
    }

    /**
     * Unfollow the given username.
     */
    @DeleteMapping(FollowEndpoint.USERNAME)
    public Mono<Void> unfollow(@AuthenticationPrincipal UUID userId, @PathVariable String usernameToFollow) {
        return followService.unfollow(userId, usernameToFollow);
    }

    /**
     * List followers of the given username, paged.
     * GET /follows/{username}/followers?page=0&size=20
     */
    @GetMapping(FollowEndpoint.FOLLOWERS)
    public Flux<FollowListItem> getFollowers(@PathVariable String username,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "20") int size) {
        int cappedSize = Math.min(size, 100);
        return followService.getFollowers(username, page, cappedSize);
    }

    /**
     * List users the given username is following, paged.
     * GET /follows/{username}/following?page=0&size=20
     */
    @GetMapping(FollowEndpoint.FOLLOWERS)
    public Flux<FollowListItem> getFollowing(@PathVariable String username,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "20") int size) {
        int cappedSize = Math.min(size, 100);
        return followService.getFollowing(username, page, cappedSize);
    }
}
