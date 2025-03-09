package org.demkiv.persistance.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "person_status")
public class PersonStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "isFound")
    private boolean isFound;
    @Column(name = "createdAt")
    private LocalDateTime createdAt;
    @Column(name = "removedAt")
    private LocalDateTime removedAt;

    @OneToOne
    @JoinColumn(name="person_id", nullable=false)
    private Person person;
}
