package org.demkiv.persistance.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(name = "fullname")
    private String fullname;
    @Column(name = "birthday")
    private Date birthday;
    @Column(name = "description")
    private String description;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "finder_id", referencedColumnName = "id")
    private Finder finder;
    @OneToMany(mappedBy="person")
    private Set<Posts> posts;
    @OneToMany(mappedBy="person")
    private Set<Photo> photos;
}
