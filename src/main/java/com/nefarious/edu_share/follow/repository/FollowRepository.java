package com.nefarious.edu_share.follow.repository;

import com.nefarious.edu_share.follow.entity.Follow;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface FollowRepository extends ReactiveCrudRepository<Follow, Long> {
    Mono<Void> deleteByFollowerIdAndFolloweeId(UUID follower, UUID followee);
    Mono<Boolean> existsByFollowerIdAndFolloweeId(UUID follower, UUID followee);
    Flux<Follow> findAllByFollowerId(UUID followerId);
    Flux<Follow> findAllByFolloweeId(UUID followeeId);
}
