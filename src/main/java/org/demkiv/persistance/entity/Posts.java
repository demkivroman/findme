package org.demkiv.persistance.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "posts")
public class Posts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "post")
    @NotNull
    @NotBlank
    private String post;
    @Column(name = "time")
    @NotNull
    private LocalDateTime time;
    @ManyToOne
    @JoinColumn(name="person_id")
    private Person person;
}
