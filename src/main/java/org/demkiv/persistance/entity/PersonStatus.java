package org.demkiv.persistance.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "person_status")
public class PersonStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private boolean isFound;
    private LocalDateTime createdAt;
    private LocalDateTime removedAt;

    @OneToOne
    @JoinColumn(name="person_id", nullable=false)
    private Person person;
}
