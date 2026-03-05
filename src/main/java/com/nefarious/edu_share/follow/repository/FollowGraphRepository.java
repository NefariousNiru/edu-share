package com.nefarious.edu_share.follow.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.ReactiveNeo4jClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class FollowGraphRepository {

    private final ReactiveNeo4jClient neo4j;

    public Mono<UUID> ensureUserNode(UUID userId) {
        return neo4j.query("""
                        MERGE (u:User {id: $userId})
                        RETURN u.id AS id
                        """)
                .bindAll(Map.of("userId", userId.toString()))
                .fetchAs(UUID.class)
                .mappedBy((t, r) -> UUID.fromString(r.get("id").asString()))
                .one();
    }

    public Mono<Void> follow(UUID followerId, UUID followeeId) {
        return neo4j.query("""
                        MATCH (me:User {id: $followerId})
                        MATCH (them:User {id: $followeeId})
                        MERGE (me)-[r:FOLLOWS]->(them)
                        ON CREATE SET r.createdAt = datetime()
                        """)
                .bindAll(Map.of(
                        "followerId", followerId.toString(),
                        "followeeId", followeeId.toString()
                ))
                .run()
                .then();
    }

    public Mono<Void> unfollow(UUID followerId, UUID followeeId) {
        return neo4j.query("""
                        MATCH (:User {id: $followerId})-[r:FOLLOWS]->(:User {id: $followeeId})
                        DELETE r
                        """)
                .bindAll(Map.of(
                        "followerId", followerId.toString(),
                        "followeeId", followeeId.toString()
                ))
                .run()
                .then();
    }

    public Mono<Boolean> exists(UUID followerId, UUID followeeId) {
        return neo4j.query("""
                        MATCH (:User {id: $followerId})-[r:FOLLOWS]->(:User {id: $followeeId})
                        RETURN count(r) > 0 AS exists
                        """)
                .bindAll(Map.of(
                        "followerId", followerId.toString(),
                        "followeeId", followeeId.toString()
                ))
                .fetchAs(Boolean.class)
                .mappedBy((t, r) -> r.get("exists").asBoolean())
                .one()
                .defaultIfEmpty(false);
    }

    public Flux<UUID> followingIds(UUID userId, long offset, long size) {
        return neo4j.query("""
                        MATCH (:User {id: $userId})-[r:FOLLOWS]->(u:User)
                        RETURN u.id AS id
                        ORDER BY r.createdAt DESC
                        SKIP $offset LIMIT $size
                        """)
                .bindAll(Map.of(
                        "userId", userId.toString(),
                        "offset", offset,
                        "size", size
                ))
                .fetchAs(UUID.class)
                .mappedBy((t, r) -> UUID.fromString(r.get("id").asString()))
                .all();
    }

    public Flux<UUID> followerIds(UUID userId, long offset, long size) {
        return neo4j.query("""
                        MATCH (u:User)-[r:FOLLOWS]->(:User {id: $userId})
                        RETURN u.id AS id
                        ORDER BY r.createdAt DESC
                        SKIP $offset LIMIT $size
                        """)
                .bindAll(Map.of(
                        "userId", userId.toString(),
                        "offset", offset,
                        "size", size
                ))
                .fetchAs(UUID.class)
                .mappedBy((t, r) -> UUID.fromString(r.get("id").asString()))
                .all();
    }
}