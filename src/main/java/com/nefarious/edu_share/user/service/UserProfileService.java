package com.nefarious.edu_share.user.service;

import com.nefarious.edu_share.auth.util.enums.AuthError;
import com.nefarious.edu_share.shared.exceptions.BusinessException;
import com.nefarious.edu_share.user.dto.PublicUserProfile;
import com.nefarious.edu_share.user.dto.UpdateUserProfileRequest;
import com.nefarious.edu_share.user.dto.UserProfile;
import com.nefarious.edu_share.user.entity.User;
import com.nefarious.edu_share.user.mapper.UserMapper;
import com.nefarious.edu_share.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    /** Public profile by username */
    public Mono<PublicUserProfile> getPublicProfile(String username) {
        return userRepository.findByUsername(username)
                .map(userMapper::toPublicUserProfile);
    }

    /** Principal Profile */
    public Mono<UserProfile> getMyProfile(UUID userId) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new BusinessException(AuthError.USER_NOT_EXISTS)))
                .map(userMapper::toMyProfile);
    }

    public Mono<UserProfile> updateProfile(UUID userId, @Valid UpdateUserProfileRequest dto) {
        return userRepository.findById(userId)
                .flatMap(user -> {
                    userMapper.updateEntityFromDto(dto, user);
                    return userRepository.save(user);
                })
                .map(userMapper::toMyProfile);
    }
}
