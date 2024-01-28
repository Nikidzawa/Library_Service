package ru.nikidzawa.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

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

    Long ownerID;
}
