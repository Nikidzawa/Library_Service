package org.example.dto;

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
    String surname;
}
