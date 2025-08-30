package org.demkiv.persistance.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
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

    @ManyToOne
    @JoinColumn(name="finder_id")
    private Finder finder;
    @OneToMany(mappedBy="person")
    private Set<Posts> posts;
    @OneToMany(mappedBy="person")
    private Set<Photo> photos;
    @OneToOne(mappedBy = "person")
    private PersonStatus personStatus;
}
