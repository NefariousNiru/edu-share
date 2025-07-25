package com.nefarious.edu_share.follow.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.util.UUID;

@Table("follow")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Follow {
    @Id
    private Long id;

    @Column("follower_id")
    private UUID followerId;

    @Column("followee_id")
    private UUID followeeId;
}
