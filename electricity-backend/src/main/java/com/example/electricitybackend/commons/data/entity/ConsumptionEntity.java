package com.example.electricitybackend.commons.data.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "consumption")
public class ConsumptionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String meterSerialNumber;
    private Double currentReading;
    private Double previousReading;
    private Double totalConsumption;
    private Double electricityRate;
    private Double monthlyCost;
    private String imageUrl;
    private LocalDateTime electricityMonth;
    @CreationTimestamp
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "household_id")
    private HouseholdEntity household;

}
