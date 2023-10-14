package org.example.store.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table (name = "reader")
public class ReaderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    @Column (name = "name")
    String name;

    @Column (name = "surname")
    String surname;

    @Builder.Default
    @OneToMany(mappedBy = "reader")
    List <BookEntity> Books = new ArrayList<>();
}
