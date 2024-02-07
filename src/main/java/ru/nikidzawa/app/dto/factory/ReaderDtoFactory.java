package ru.nikidzawa.app.dto.factory;

import org.springframework.stereotype.Component;
import ru.nikidzawa.app.dto.ReaderDto;
import ru.nikidzawa.app.store.entities.ReaderEntity;

@Component
public class ReaderDtoFactory {
    public ReaderDto createReader (ReaderEntity reader) {
       return ReaderDto.builder()
               .id(reader.getId())
               .name(reader.getName())
               .nickname(reader.getNickname())
               .mail(reader.getMail())
               .role(reader.getRole().name())
               .build();
    }
}