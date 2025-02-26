package org.demkiv.persistance.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;



@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "posts")
public class Posts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "post")
    @NotNull
    @NotBlank
    private String post;
    @Column(name = "author")
    private String author;
    @Column(name = "time")
    @NotNull
    private LocalDateTime time;
    @ManyToOne
    @JoinColumn(name="person_id")
    private Person person;
}
