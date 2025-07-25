package com.nefarious.edu_share.user.mapper;
import com.nefarious.edu_share.user.dto.PublicUserProfile;
import com.nefarious.edu_share.user.dto.UpdateUserProfileRequest;
import com.nefarious.edu_share.user.dto.UserProfile;
import com.nefarious.edu_share.user.entity.User;
import org.mapstruct.*;

/**
 * MapStruct mapper to convert between User entity and DTOs.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    /** Entity → UserProfile DTO */
    PublicUserProfile toPublicUserProfile(User user);

    /** Map entity → private userProfile DTO */
    @Mapping(target = "dateOfBirth", source = "dateOfBirth")
    @Mapping(target = "email",       source = "email")
    UserProfile toMyProfile(User user);

    /** Update only the editable fields on an existing entity */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UpdateUserProfileRequest dto, @MappingTarget User entity);
}
