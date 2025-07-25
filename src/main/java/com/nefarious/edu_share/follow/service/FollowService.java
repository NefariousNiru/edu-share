package com.nefarious.edu_share.follow.service;

import com.nefarious.edu_share.follow.dto.FollowListItem;
import com.nefarious.edu_share.follow.entity.Follow;
import com.nefarious.edu_share.follow.repository.FollowRepository;
import com.nefarious.edu_share.user.entity.User;
import com.nefarious.edu_share.user.service.UserService;
import lombok.AllArgsConstructor;
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
    private final FollowRepository followRepository;
    private final UserService userService;

    /** Follow a user */
    public Mono<Void> follow(UUID userId, String usernameToFollow) {
        return userService.getByUsername(usernameToFollow)
                .flatMap(userToFollow -> {
                    Follow follow = new Follow(null, userId, userToFollow.getId());
                    return followRepository.save(follow).then();
                });
    }

    /** Unfollow a user */
    public Mono<Void> unfollow(UUID userId, String usernameToUnfollow) {
        return userService.getByUsername(usernameToUnfollow)
                .flatMap(userToUnfollow -> followRepository.deleteByFollowerIdAndFolloweeId(userId, userToUnfollow.getId()));
    }

    /** Get a list of users, user is following */
    public Flux<FollowListItem> getFollowing(String username, int page, int size) {
        int offset = page * size;
        return userService.getByUsername(username)
                .flatMapMany(user -> followRepository.findAllByFollowerId(user.getId()).skip(offset).take(size))
                .flatMap(follow -> userService.getById(follow.getFolloweeId()).map(this::toFollowListItem));
    }

    /** Get a list of users that follow user */
    public Flux<FollowListItem> getFollowers(String username, int page, int size) {
        int offset = page * size;
        return userService.getByUsername(username)
                .flatMapMany(user -> followRepository.findAllByFolloweeId(user.getId()).skip(offset).take(size))
                .flatMap(follow -> userService.getById(follow.getFollowerId()).map(this::toFollowListItem));
    }


    /** Map full User → follow list‐item DTO */
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
