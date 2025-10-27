package org.demkiv.persistance.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "photo")
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "url")
    @NotNull
    private String url;
    @Column(name = "converted_url")
    @NotNull
    private String convertedUrl;

    @ManyToOne
    @JoinColumn(name="person_id", nullable=false)
    private Person person;
}
