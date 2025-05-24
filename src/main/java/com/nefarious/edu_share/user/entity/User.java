package com.nefarious.edu_share.user.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.util.UUID;

/** Represents a User Domain with properties */
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    /** Primary key: generated UUID */
    @Id
    @Column("id")
    private UUID id;

    /** Unique email address, non-null and unique */
    @NotNull
    @Column("email")
    private String email;

    /** Hashed password, non-null */
    @NotBlank
    @Column("password")
    private String password;

    /** Display name/login name, non-null and unique */
    @Size(max = 30)
    @NotBlank
    @Column("username")
    private String username;

    /** First Name, non-null */
    @Size(max = 30)
    @NotBlank
    @Column("first_name")
    private String firstName;

    /** Last Name, non-null */
    @Size(max = 30)
    @NotBlank
    @Column("last_name")
    private String lastName;

    /** User’s date of birth, stored as SQL DATE */
    @NotNull
    @Column("date_of_birth")
    private LocalDate dateOfBirth;

    /** Whether the user has verified their email—default is false */
    @Column("is_email_verified")
    private boolean isEmailVerified;

    @Size(max = 250)
    @Column("bio")
    private String bio;

    @Column("profile_image_url")
    private String profileImageUrl;

    @Size(max = 50)
    @Column("organization")
    private String organization;

    @Size(max = 50)
    @Column("school")
    private String school;

    @Size(max = 50)
    @Column("position")
    private String position;
}
