package ru.nikidzawa.factory;

import ru.nikidzawa.dto.ReaderDto;
import ru.nikidzawa.store.entities.ReaderEntity;
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
