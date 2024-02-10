package ru.nikidzawa.app.store.dto;

import jakarta.persistence.Column;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReaderDto {
    Long id;

    @NonNull
    String name;

    @NonNull
    @Column(unique = true)
    String nickname;

    String mail;

    @NonNull
    String role;
}
