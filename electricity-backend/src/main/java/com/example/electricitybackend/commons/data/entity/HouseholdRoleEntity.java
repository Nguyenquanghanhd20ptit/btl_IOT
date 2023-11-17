package com.example.electricitybackend.commons.data.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "household_role")
public class HouseholdRoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "household_id")
    private HouseholdEntity household;
    @ManyToOne
    @JoinColumn(name = "role_id")
    private RoleEntity role;
    @CreationTimestamp
    private LocalDateTime createdAt;
}
