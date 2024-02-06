package ru.nikidzawa.store.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "readers")
public class ReaderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;

    String nickname;

    String mail;

    String password;

    Roles role;

    @OneToMany(mappedBy = "reader", fetch = FetchType.LAZY)
    List <BookEntity> books;
}
