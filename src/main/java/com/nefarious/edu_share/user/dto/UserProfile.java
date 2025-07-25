package com.nefarious.edu_share.user.dto;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile extends PublicUserProfile {
    private LocalDate dateOfBirth;
    private String email;
}
