package org.demkiv.persistance.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Builder
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
    @JoinColumn(name="person_id")
    private Person person;
}
