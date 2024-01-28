package ru.nikidzawa.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import ru.nikidzawa.dto.BookDto;
import ru.nikidzawa.dto.ReaderDto;
import ru.nikidzawa.dto.factory.BookDtoFactory;
import ru.nikidzawa.dto.factory.ReaderDtoFactory;
import ru.nikidzawa.responses.OKResponse;
import ru.nikidzawa.responses.exceptions.BadRequestException;
import ru.nikidzawa.responses.exceptions.Exception;
import ru.nikidzawa.responses.exceptions.NotFoundException;
import ru.nikidzawa.store.entities.BookEntity;
import ru.nikidzawa.store.entities.ReaderEntity;
import ru.nikidzawa.store.repositoreis.BooksRepository;
import ru.nikidzawa.store.repositoreis.ReadersRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Tag(name = "Читатели", description = "Управление читателями")
@RequiredArgsConstructor
@Transactional
@FieldDefaults (level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
public class ReadersController {

    ReadersRepository readersRepository;

    BookDtoFactory bookDtoFactory;

    ReaderDtoFactory readerDtoFactory;

    BooksRepository booksRepository;

    public static final String CREATE_READER = "/api/readers/create";
    public static final String READER_INFO = "/api/readers/{id}";
    public static final String READERS_LIST = "api/readers";
    public static final String READER_BOOKS_LIST = "/api/readers/{id}/books";
    public static final String PATCH_READER = "/api/readers/edit/{id}";
    public static final String DELETE_READER = "/api/readers/delete/{id}";

    @Operation(summary = "Зарегистрировать читателя")
    @ApiResponse (
            responseCode = "200",
            description = "Читатель зарегистрирован",
            content = {
                    @Content (
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ReaderDto.class))
                    )
            })
    @PostMapping(CREATE_READER)
    public ReaderDto createReader (@RequestParam (value = "name") String name,
                                   @RequestParam (value = "surname") String surname) {
        return readerDtoFactory.createReader(readersRepository.saveAndFlush(
                ReaderEntity.builder()
                        .name(name)
                        .surname(surname)
                        .build()
                )
        );
    }

    @Operation(summary = "Изменить био читателя")
    @ApiResponse (
            responseCode = "200",
            description = "Изменения внесены",
            content = {
                    @Content (
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ReaderDto.class))
                    )
            })
    @ApiResponse (
            responseCode = "400",
            description = "Данные не были изменены",
            content = {
                    @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Exception.class))
                    )}
    )
    @ApiResponse (
            responseCode = "404",
            description = "Пользователь не найден",
            content = {
                    @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Exception.class))
                    )}
    )
    @PatchMapping(PATCH_READER)
    public ReaderDto editReader (@PathVariable (value = "id") Long id,
                                @RequestParam (value = "name", required = false) Optional <String> name,
                                @RequestParam (value = "surname", required = false) Optional <String> surname)
    {
        Optional <ReaderEntity> readerEntity = readersRepository.findById(id);
        return readerEntity.map(reader -> {
            boolean hasBeenEdited = false;
            if (name.isPresent()) {
                hasBeenEdited = true;
                reader.setName(name.get());
            }
            if (surname.isPresent()) {
                hasBeenEdited = true;
                reader.setSurname(surname.get());
            }
            if (hasBeenEdited) {
                return readerDtoFactory.createReader(readersRepository.saveAndFlush(reader));
            }
            throw new BadRequestException("Данные не были изменены");
        }).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    @Operation(summary = "Получить информацию о читателе")
    @ApiResponse (
            responseCode = "200",
            description = "Читатель получен",
            content = {
                    @Content (
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ReaderDto.class))
                    )
            })
    @ApiResponse (
            responseCode = "404",
            description = "Пользователь не найден",
            content = {
                    @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Exception.class))
                    )}
    )
    @GetMapping(READER_INFO)
    public ReaderDto readerInfo (@PathVariable (value = "id") Long id) {
        return readerDtoFactory
                .createReader(readersRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден")));
    }

    @Operation(summary = "Получить книги читателя")
    @ApiResponse (
            responseCode = "200",
            description = "Книги получены",
            content = {
                    @Content (
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ReaderDto.class))
                    )
            })
    @ApiResponse (
            responseCode = "404",
            description = "Cущности не найдены",
            content = {
                    @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Exception.class))
                    )}
    )
    @GetMapping(READER_BOOKS_LIST)
    public List<BookDto> readerBooks (@PathVariable (value = "id") Long readerId)
    {
        ReaderEntity reader = readersRepository.findById(readerId)
                .orElseThrow(() -> new NotFoundException("Читатель не найден"));
        List<BookEntity> bookEntities = reader.getBooks();
        if (bookEntities.isEmpty()) {
            throw new NotFoundException("У читателя нет книг");
        } else {
            return bookEntities.stream()
                    .map(bookDtoFactory::createBook)
                    .collect(Collectors.toList());
        }
    }

    @Operation(summary = "Получить всех читателей")
    @ApiResponse (
            responseCode = "200",
            description = "Читатели получены",
            content = {
                    @Content (
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ReaderDto.class))
                    )
            })
    @ApiResponse (
            responseCode = "404",
            description = "Читатели не найдены",
            content = {
                    @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Exception.class))
                    )}
    )
    @GetMapping(READERS_LIST)
    public List<ReaderDto> allReaders ()
    {
        List<ReaderEntity> readerEntities = readersRepository.findAll();
        if (readerEntities.isEmpty()) {
            throw new NotFoundException("Читатели не найдены");
        }
        return readerEntities.stream()
                .map(readerDtoFactory::createReader)
                .toList();
    }

    @Operation(summary = "Удалить читателя")
    @ApiResponse (
            responseCode = "200",
            description = "Читатель удалён",
            content = {
                    @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = OKResponse.class))
                    )}
    )
    @ApiResponse (
            responseCode = "404",
            description = "Читатель не найден",
            content = {
                    @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Exception.class))
                    )}
    )
    @DeleteMapping(DELETE_READER)
    public OKResponse deleteReader(@PathVariable (value = "id") Long id) {
        Optional<ReaderEntity> readerEntity = readersRepository.findById(id);
        return readerEntity.map(reader -> {
            reader.getBooks().forEach(bookEntity -> {
                bookEntity.setReader(null);
                booksRepository.saveAndFlush(bookEntity);
            });
            readersRepository.delete(reader);
            return OKResponse.builder()
                    .code(200)
                    .message("Читатель удалён")
                    .build();
        }).orElseThrow(() -> new NotFoundException("Читатель не найден"));
    }
}
