package com.nefarious.edu_share.follow.service;

import com.nefarious.edu_share.follow.dto.FollowListItem;
import com.nefarious.edu_share.follow.repository.FollowGraphRepository;
import com.nefarious.edu_share.follow.util.enums.FollowError;
import com.nefarious.edu_share.shared.exceptions.BusinessException;
import com.nefarious.edu_share.user.entity.User;
import com.nefarious.edu_share.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FollowService {
    private final FollowGraphRepository followGraphRepository;
    private final UserService userService;

    /**
     * Follow a user (idempotent)
     */
    public Mono<Void> follow(UUID userId, String usernameToFollow) {
        return userService.getByUsername(usernameToFollow)
                .flatMap(userToFollow -> {
                    if (userId.equals(userToFollow.getId())) {
                        return Mono.error(new BusinessException(FollowError.CANNOT_FOLLOW_SELF));
                    }

                    // Ensure both nodes exist, then MERGE relationship (no duplicates)
                    return Mono.when(
                                    followGraphRepository.ensureUserNode(userId),
                                    followGraphRepository.ensureUserNode(userToFollow.getId())
                            )
                            .then(followGraphRepository.follow(userId, userToFollow.getId()))
                            .then();
                });
    }

    /**
     * Unfollow a user
     */
    public Mono<Void> unfollow(UUID userId, String usernameToUnfollow) {
        return userService.getByUsername(usernameToUnfollow)
                .flatMap(userToUnfollow ->
                        followGraphRepository.unfollow(userId, userToUnfollow.getId()).then()
                );
    }

    /**
     * Get a list of users this user is following (no N+1)
     */
    public Flux<FollowListItem> getFollowing(String username, int page, int size) {
        long offset = (long) page * size;

        return userService.getByUsername(username)                 // Mono<User>
                .flatMapMany(u -> followGraphRepository
                        .followingIds(u.getId(), offset, size))    // Flux<UUID>
                .collectList()                                     // Mono<List<UUID>>
                .flatMapMany(userService::getByIds)                // Flux<User>
                .map(this::toFollowListItem);
    }

    /**
     * Get a list of users that follow this user (no N+1)
     */
    public Flux<FollowListItem> getFollowers(String username, int page, int size) {
        long offset = (long) page * size;

        return userService.getByUsername(username)                 // Mono<User>
                .flatMapMany(u -> followGraphRepository
                        .followerIds(u.getId(), offset, size))     // Flux<UUID>
                .collectList()                                     // Mono<List<UUID>>
                .flatMapMany(userService::getByIds)                // Flux<User>
                .map(this::toFollowListItem);
    }

    private FollowListItem toFollowListItem(User u) {
        String aff = u.getOrganization() != null && !u.getOrganization().isBlank()
                ? u.getOrganization()
                : u.getSchool();

        return FollowListItem.builder()
                .username(u.getUsername())
                .profileImageUrl(u.getProfileImageUrl())
                .affiliation(aff)
                .build();
    }
}
