package com.example.electricitybackend.commons.data.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "household")
public class HouseholdEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String username;
    private String password;
    private String householdName;
    private String address;
    private String phoneNumber;
    private String meterSerialNumber;
    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "household")
    private List<ConsumptionEntity> consumptions;
    @PostPersist
    public void generateMeterSerialNumber() {
        if (this.meterSerialNumber == null) {
            this.meterSerialNumber = String.format("%06d", id);
        }
    }
}
