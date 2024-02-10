package ru.nikidzawa.app.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.nikidzawa.app.configs.bookkeepingSystem.BookkeepingService;
import ru.nikidzawa.app.configs.redis.annotation.UpdateCacheIfChangedKey;
import ru.nikidzawa.app.responses.OKResponse;
import ru.nikidzawa.app.responses.exceptions.BadRequestException;
import ru.nikidzawa.app.responses.exceptions.NotFoundException;
import ru.nikidzawa.app.store.entities.BookEntity;
import ru.nikidzawa.app.store.entities.ReaderEntity;
import ru.nikidzawa.app.store.repositoreis.BooksRepository;
import ru.nikidzawa.app.store.repositoreis.ReadersRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@CacheConfig(cacheNames = "book")
public class BookService {

    BooksRepository booksRepository;

    ReadersRepository readersRepository;

    BookkeepingService bookkeepingService;

    @Cacheable(key = "#name")
    public BookEntity createBook (String name, String author, String description) {
        booksRepository.findFirstByName(name).ifPresent(existBooks -> {
            throw new BadRequestException("Книга с таким именем уже существует");
        });
        return booksRepository.saveAndFlush(
                BookEntity.builder()
                        .name(name)
                        .author(author)
                        .description(description)
                        .build()
        );
    }

    public List<BookEntity> getAllBooks () {
        List<BookEntity> bookEntities = booksRepository.findAll();
        if (bookEntities.isEmpty()) {
            throw new NotFoundException("Книги не найдены");
        }
        return bookEntities;
    }

    @Cacheable(key = "#bookName")
    public BookEntity getBook (String bookName) {
        return booksRepository.findFirstByName(bookName).orElseThrow(() -> new NotFoundException("Книга не найдена"));
    }

    @UpdateCacheIfChangedKey(prefix = "book")
    public BookEntity editBook (String bookName, Optional<String> newName, Optional <String> author, Optional<String> description) {
        return booksRepository.findFirstByName(bookName).map(bookEntity -> {
            boolean hasBeenEdited = false;
            if (newName.isPresent()) {
                hasBeenEdited = true;
                bookEntity.setName(newName.get());
            }
            if (author.isPresent()) {
                hasBeenEdited = true;
                bookEntity.setAuthor(author.get());
            }
            if (description.isPresent()) {
                hasBeenEdited = true;
                bookEntity.setDescription(description.get());
            }
            if (hasBeenEdited) {
                booksRepository.saveAndFlush(bookEntity);
                return bookEntity;
            } else throw new BadRequestException("Ни один из параметров не был изменён");
        }).orElseThrow(() -> new NotFoundException("Книга не найдена"));
    }

    @CachePut (key = "#bookName")
    public BookEntity setOwner (String bookName, String readerNickname, Long days) {
        ReaderEntity reader = readersRepository.findFirstByNickname(readerNickname)
                .orElseThrow(() -> new NotFoundException("Пользователя с таким именем не существует"));
        BookEntity book = booksRepository.findFirstByName(bookName)
                .orElseThrow(() -> new NotFoundException("Книги с указанным id не существует"));
        if (book.getReader() != null) {throw new BadRequestException("У книги уже есть владелец");}

        book.setReader(reader);
        book.setIssue(LocalDateTime.now());
        book.setDeadLine(LocalDateTime.now().plusDays(days));
        bookkeepingService.takeBook(days, book, reader);
        return booksRepository.saveAndFlush(book);
    }

    @CachePut (key = "#bookName")
    public BookEntity removeOwner(String bookName) {
        return booksRepository.saveAndFlush(
                booksRepository.findFirstByName(bookName)
                        .map(bookEntity -> {
                            if (bookEntity.getReader() == null) {throw new BadRequestException("У книги нет владельца");}
                            bookEntity.setReader(null);
                            bookEntity.setIssue(null);
                            bookEntity.setDeadLine(null);
                            bookkeepingService.handOverBook(bookEntity.getId());
                            return bookEntity;
                        }).orElseThrow(() -> new NotFoundException("Книги не существует"))
        );
    }

    @CacheEvict (key = "#bookName")
    public OKResponse deleteBook (String bookName) {
        return booksRepository.findFirstByName(bookName).map(bookEntity -> {
            ReaderEntity reader = bookEntity.getReader();
            if (reader != null) {
                reader.getBooks().remove(bookEntity);
                updateCash(reader);
            }
            booksRepository.delete(bookEntity);
            return OKResponse.builder()
                    .code(200)
                    .message("Книга удалена из базы данных")
                    .build();
        }).orElseThrow(() -> new NotFoundException("Указанной книги не существует"));
    }

    @CachePut (key = "#result.nickname")
    public ReaderEntity updateCash(ReaderEntity reader) {
        return readersRepository.saveAndFlush(reader);
    }
}
