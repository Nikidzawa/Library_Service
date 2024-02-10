package ru.nikidzawa.app.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.nikidzawa.app.configs.redis.annotation.UpdateCacheIfChangedKey;
import ru.nikidzawa.app.responses.OKResponse;
import ru.nikidzawa.app.responses.exceptions.BadRequestException;
import ru.nikidzawa.app.responses.exceptions.NotFoundException;
import ru.nikidzawa.app.store.entities.BookEntity;
import ru.nikidzawa.app.store.entities.ReaderEntity;
import ru.nikidzawa.app.store.entities.Roles;
import ru.nikidzawa.app.store.repositoreis.BooksRepository;
import ru.nikidzawa.app.store.repositoreis.ReadersRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@CacheConfig(cacheNames = "reader")
public class ReaderService {

    ReadersRepository readersRepository;

    BooksRepository booksRepository;

    PasswordEncoder passwordEncoder;

    @Cacheable(key = "#readerNickname")
    public ReaderEntity createReader (String name, String readerNickname, String password, String mail) {
        readersRepository.findFirstByNickname(readerNickname)
                .ifPresent(existingReader -> {throw new BadRequestException("Читатель с таким именем уже существует");});
        return readersRepository.saveAndFlush(ReaderEntity.builder()
                .name(name)
                .nickname(readerNickname)
                .password(passwordEncoder.encode(password))
                .mail(mail)
                .role(Roles.READER)
                .build());
    }

    @UpdateCacheIfChangedKey(prefix = "reader")
    public ReaderEntity editReader (String readerNickname, Optional <String> newNickname, Optional<String> name) {
        return readersRepository.findFirstByNickname(readerNickname).map(reader -> {
            boolean hasBeenEdited = false;
            if (name.isPresent()) {
                hasBeenEdited = true;
                reader.setName(name.get());
            }
            if (newNickname.isPresent()) {
                hasBeenEdited = true;
                reader.setNickname(newNickname.get());
            }
            if (hasBeenEdited) {
                return readersRepository.saveAndFlush(reader);
            } else throw new BadRequestException("Данные не были изменены");
        }).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    @Cacheable(key = "#readerNickname")
    public ReaderEntity getReader (String readerNickname) {
        return readersRepository.findFirstByNickname(readerNickname).orElseThrow(() -> new NotFoundException("Пользователь не найден"));

    }

    public List<ReaderEntity> getAllReaders () {
        List<ReaderEntity> readerEntities = readersRepository.findAll();
        if (readerEntities.isEmpty()) {throw new NotFoundException("Читатели не найдены");}
        return readerEntities;
    }

    public List<BookEntity> getReaderBooks (String readerNickname) {
        return readersRepository.findFirstByNickname(readerNickname).map(reader -> {
            List<BookEntity> bookEntities = reader.getBooks();
            if (bookEntities.isEmpty()) {
                throw new NotFoundException("У читателя нет книг");
            } else {
                return bookEntities;
            }
        }).orElseThrow(() -> new NotFoundException("Читателя не существует"));
    }

    @CacheEvict(key = "#readerNickname")
    public OKResponse deleteReader (String readerNickname) {
        return readersRepository.findFirstByNickname(readerNickname).map(reader -> {
            reader.getBooks().forEach(bookEntity -> {
                bookEntity.setReader(null);
                bookEntity.setDeadLine(null);
                bookEntity.setIssue(null);
                updateCash(bookEntity);
            });
            readersRepository.delete(reader);
            return OKResponse.builder()
                    .code(200)
                    .message("Читатель удалён")
                    .build();
        }).orElseThrow(() -> new NotFoundException("Читатель не найден"));
    }

    @CachePut(key = "#result.name")
    public BookEntity updateCash (BookEntity bookEntity) {
        return booksRepository.saveAndFlush(bookEntity);
    }
}
