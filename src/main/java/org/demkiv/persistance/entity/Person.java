package org.demkiv.persistance.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "finder_id", referencedColumnName = "id")
    private Finder finder;
    @OneToMany(mappedBy="person")
    private Set<Posts> posts;
    @OneToMany(mappedBy="person")
    private Set<Photo> photos;
}
