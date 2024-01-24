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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.nikidzawa.dto.ReaderDto;
import ru.nikidzawa.factory.BookDtoFactory;
import ru.nikidzawa.factory.ReaderDtoFactory;
import ru.nikidzawa.store.entities.BookEntity;
import ru.nikidzawa.store.entities.ReaderEntity;
import ru.nikidzawa.store.repositoreis.BooksRepository;
import ru.nikidzawa.store.repositoreis.ReadersRepository;
import org.springframework.web.bind.annotation.*;

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
                            mediaType = "reader.json",
                            array = @ArraySchema(schema = @Schema(implementation = ReaderDto.class))
                    )
            })
    @PostMapping(CREATE_READER)
    public ReaderDto createReader (@RequestParam (value = "name") String name,
                                   @RequestParam (value = "surname") String surname) {
        ReaderEntity readerEntity = readersRepository.saveAndFlush(
                ReaderEntity.builder()
                        .name(name)
                        .surname(surname)
                        .build());
        return readerDtoFactory.createReader(readerEntity);
    }

    @Operation(summary = "Изменить био читателя")
    @ApiResponse (
            responseCode = "200",
            description = "Изменения внесены",
            content = {
                    @Content (
                            mediaType = "reader.json",
                            array = @ArraySchema(schema = @Schema(implementation = ReaderDto.class))
                    )
            })
    @ApiResponse (
            responseCode = "400",
            description = "Данные не были изменены",
            content = {
                    @Content(
                            mediaType = "string",
                            array = @ArraySchema(schema = @Schema(implementation = String.class))
                    )}
    )
    @ApiResponse (
            responseCode = "404",
            description = "Пользователь не найден",
            content = {
                    @Content(
                            mediaType = "string",
                            array = @ArraySchema(schema = @Schema(implementation = String.class))
                    )}
    )
    @PatchMapping(PATCH_READER)
    public ResponseEntity<?> patchUser (@PathVariable (value = "id") Long id,
                                @RequestParam (value = "name", required = false) Optional <String> name,
                                @RequestParam (value = "surname", required = false) Optional <String> surname)
    {
        Optional <ReaderEntity> readerEntity = readersRepository.findById(id);
        return readerEntity.map(user -> {
            boolean hasBeenEdited = false;
            if (name.isPresent()) {
                hasBeenEdited = true;
                user.setName(name.get());
            }

            if (surname.isPresent()) {
                hasBeenEdited = true;
                user.setSurname(surname.get());
            }

            if (hasBeenEdited) {
                readersRepository.saveAndFlush(user);
                return ResponseEntity.status(HttpStatus.OK).body(readerDtoFactory.createReader(user));
            } else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Данные не были изменены");

        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Пользователь не найден"));
    }

    @Operation(summary = "Получить информацию о читателе")
    @ApiResponse (
            responseCode = "200",
            description = "Читатель получен",
            content = {
                    @Content (
                            mediaType = "reader.json",
                            array = @ArraySchema(schema = @Schema(implementation = ReaderDto.class))
                    )
            })
    @ApiResponse (
            responseCode = "404",
            description = "Пользователь не найден",
            content = {
                    @Content(
                            mediaType = "string",
                            array = @ArraySchema(schema = @Schema(implementation = String.class))
                    )}
    )
    @GetMapping(READER_INFO)
    public ResponseEntity<?> ShowUserInformation (@PathVariable (value = "id") Long id) {
        Optional<ReaderEntity> reader = readersRepository.findById(id);
        return reader.isPresent() ?
                ResponseEntity.status(HttpStatus.OK).body(readerDtoFactory.createReader(reader.get())) :
                ResponseEntity.status(HttpStatus.NOT_FOUND).body("Пользователь не найден");
    }

    @Operation(summary = "Получить книги читателя")
    @ApiResponse (
            responseCode = "200",
            description = "Книги получены",
            content = {
                    @Content (
                            mediaType = "reader.json",
                            array = @ArraySchema(schema = @Schema(implementation = ReaderDto.class))
                    )
            })
    @ApiResponse (
            responseCode = "404",
            description = "У читателя нет книг",
            content = {
                    @Content(
                            mediaType = "string",
                            array = @ArraySchema(schema = @Schema(implementation = String.class))
                    )}
    )
    @GetMapping(READER_BOOKS_LIST)
    public ResponseEntity<?> userList (@PathVariable (value = "id") Long readerId)
    {
        List<BookEntity> bookEntities = booksRepository.findAllByReader_Id(readerId);
        if (bookEntities.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("У читателя нет книг");
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(bookEntities.stream()
                    .map(bookDtoFactory::createBook)
                    .collect(Collectors.toList()));
        }
    }

    @Operation(summary = "Получить всех читателей")
    @ApiResponse (
            responseCode = "200",
            description = "Читатели получены",
            content = {
                    @Content (
                            mediaType = "reader.json",
                            array = @ArraySchema(schema = @Schema(implementation = ReaderDto.class))
                    )
            })
    @ApiResponse (
            responseCode = "404",
            description = "Читатели не найдены",
            content = {
                    @Content(
                            mediaType = "string",
                            array = @ArraySchema(schema = @Schema(implementation = String.class))
                    )}
    )
    @GetMapping(READERS_LIST)
    public ResponseEntity<?> readersList ()
    {
        List<ReaderEntity> readerEntities = readersRepository.findAll();
        if (readerEntities.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Читатели не найдены");
        }
        List<ReaderDto> readerDtoList = readerEntities.stream()
                .map(readerDtoFactory::createReader)
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(readerDtoList);
    }

    @Operation(summary = "Удалить читателя")
    @ApiResponse (
            responseCode = "200",
            description = "Читатель удалён",
            content = {
                    @Content(
                            mediaType = "string",
                            array = @ArraySchema(schema = @Schema(implementation = String.class))
                    )}
    )
    @ApiResponse (
            responseCode = "404",
            description = "Читатель не найден",
            content = {
                    @Content(
                            mediaType = "string",
                            array = @ArraySchema(schema = @Schema(implementation = String.class))
                    )}
    )
    @DeleteMapping(DELETE_READER)
    public ResponseEntity<String> deleteReader(@PathVariable (value = "id") Long id) {
        Optional<ReaderEntity> readerEntity = readersRepository.findById(id);

        return readerEntity.map(user -> {
            readersRepository.delete(user);
            return ResponseEntity.status(HttpStatus.OK).body("Читатель удалён");
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Читатель не найден"));
    }
}
