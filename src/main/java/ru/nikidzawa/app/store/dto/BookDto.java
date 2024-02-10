package ru.nikidzawa.app.store.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookDto {
    Long id;

    @NonNull
    String name;

    @NonNull
    String author;

    @NonNull
    String description;

    LocalDateTime issue;

    LocalDateTime deadLine;

    String owner;
}
