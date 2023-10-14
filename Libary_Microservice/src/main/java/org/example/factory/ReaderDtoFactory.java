package org.example.factory;

import org.example.dto.ReaderDto;
import org.example.store.entities.ReaderEntity;
import org.springframework.stereotype.Component;

@Component
public class ReaderDtoFactory {
    public ReaderDto createReader (ReaderEntity reader) {
       return ReaderDto.builder()
               .id(reader.getId())
               .name(reader.getName())
               .surname(reader.getSurname())
               .build();
    }
}
