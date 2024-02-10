package ru.nikidzawa.app.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import ru.nikidzawa.app.responses.exceptions.NotFoundException;
import ru.nikidzawa.app.store.entities.ReaderEntity;
import ru.nikidzawa.app.store.entities.Roles;
import ru.nikidzawa.app.store.repositoreis.ReadersRepository;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@CacheConfig(cacheNames = "reader")
@Service
public class RolesService {

    ReadersRepository readersRepository;

    @CachePut(key = "#readerNickname")
    public ReaderEntity setAdmRole (String readerNickname) {
        return readersRepository.findFirstByNickname(readerNickname).map(reader -> {
            reader.setRole(Roles.ADMIN);
            return readersRepository.saveAndFlush(reader);
        }).orElseThrow(() -> new NotFoundException("Пользователя не существует"));
    }

    @CachePut(key = "#readerNickname")
    public ReaderEntity setReaderRole (String readerNickname) {
        return readersRepository.findFirstByNickname(readerNickname).map(reader -> {
            reader.setRole(Roles.READER);
            return readersRepository.saveAndFlush(reader);
        }).orElseThrow(() -> new NotFoundException("Пользователя не существует"));
    }
}
