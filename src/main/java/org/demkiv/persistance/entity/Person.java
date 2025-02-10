package org.demkiv.persistance.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "person")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "fullname")
    private String fullname;
    @Column(name = "birthday")
    private Date birthday;
    @Column(name = "description")
    private String description;
    @Column(name = "time")
    @NotNull
    private LocalDateTime time;

    @ManyToOne
    @JoinColumn(name="finder_id", nullable=false)
    private Finder finder;
    @OneToMany(mappedBy="person")
    private Set<Posts> posts;
    @OneToMany(mappedBy="person")
    private Set<Photo> photos;
    @OneToMany(mappedBy="person")
    private Set<Thumbnail> thumbnails;
}
