package org.demkiv.persistance.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "posts")
public class Posts {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(name = "post")
    @NotNull
    private String post;
    @Column(name = "time")
    @NotNull
    private LocalDateTime time;
    @ManyToOne
    @JoinColumn(name="person_id", nullable=false)
    private Person person;
}
