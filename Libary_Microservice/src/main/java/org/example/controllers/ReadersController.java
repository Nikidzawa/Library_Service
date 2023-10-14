package org.example.controllers;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.controllers.helpers.ReaderSearchHelper;
import org.example.dto.BookDto;
import org.example.dto.ReaderDto;
import org.example.factory.BookDtoFactory;
import org.example.factory.ReaderDtoFactory;
import org.example.store.entities.BookEntity;
import org.example.store.entities.ReaderEntity;
import org.example.store.repositoreis.BooksRepository;
import org.example.store.repositoreis.ReadersRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@FieldDefaults (level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
public class ReadersController {

    ReadersRepository readersRepository;

    BookDtoFactory bookDtoFactory;

    ReaderDtoFactory readerDtoFactory;

    ReaderSearchHelper readerSearchHelper;

    BooksRepository booksRepository;
    public static final String READER_INFO = "/api/reader/{id}";
    public static final String READER_BOOKS_LIST = "/api/reader/books/{id}";
    public static final String READERS_LIST = "api/reader";
    public static final String CREATE_READER = "/api/reader/create";
    public static final String PATCH_READER = "/api/reader/{id}";
    public static final String DELETE_READER = "/api/reader/{id}";

    @PostMapping(CREATE_READER)
    public ReaderDto createReader (@RequestParam (value = "name") String name,
                                   @RequestParam (value = "surname") String surname) {
        final ReaderEntity readerEntity = ReaderEntity.builder().name(name).surname(surname).build();
        final ReaderEntity createEntity = readersRepository.saveAndFlush(readerEntity);
        return readerDtoFactory.createReader(createEntity);
    }

    @PatchMapping(PATCH_READER)
    public ReaderDto patchUser (@PathVariable (value = "id") Optional <Long> id,
                                @RequestParam (value = "name") Optional <String> name,
                                @RequestParam (value = "surname") Optional <String> surname)
    {
        final ReaderEntity readerEntity = id
                .map(readerSearchHelper::SearchReader)
                .orElse(ReaderEntity.builder().build());
        name.ifPresent(readerEntity::setName);
        surname.ifPresent(readerEntity::setSurname);

        final ReaderEntity createdReader = readersRepository.saveAndFlush(readerEntity);
        return readerDtoFactory.createReader(createdReader);
    }

    @GetMapping(READER_INFO)
    public ReaderEntity ShowUserInformation (@PathVariable (value = "id") Long id)
    {
        return readerSearchHelper.SearchReader(id);
    }

    @GetMapping(READER_BOOKS_LIST)
    public List<BookDto> userList (@PathVariable (value = "id") Long readerId)
    {
        List<BookEntity> bookEntities = booksRepository.findAllByReader_Id(readerId);
        return bookEntities.stream()
                .map(bookDtoFactory::createBook)
                .collect(Collectors.toList());
    }

    @GetMapping(READERS_LIST)
    public List<ReaderDto> readersList ()
    {
        List<ReaderEntity> readerDtoList = readersRepository.findAll();
        return readerDtoList.stream()
                .map(readerDtoFactory::createReader)
                .collect(Collectors.toList());
    }

    @DeleteMapping(DELETE_READER)
    public boolean deleteBook (@PathVariable (value = "id") Long id)
    {
        ReaderEntity reader = readerSearchHelper.SearchReader(id);
        readersRepository.delete(reader);
        return true;
    }
}
