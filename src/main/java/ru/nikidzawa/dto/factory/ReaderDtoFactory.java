package ru.nikidzawa.dto.factory;

import org.springframework.stereotype.Component;
import ru.nikidzawa.dto.ReaderDto;
import ru.nikidzawa.store.entities.ReaderEntity;

@Component
public class ReaderDtoFactory {
    public ReaderDto createReader (ReaderEntity reader) {
       return ReaderDto.builder()
               .id(reader.getId())
               .name(reader.getName())
               .nickname(reader.getNickname())
               .role(reader.getRole())
               .build();
    }
}