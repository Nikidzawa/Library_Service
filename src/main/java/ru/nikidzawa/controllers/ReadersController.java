package ru.nikidzawa.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import ru.nikidzawa.store.entities.Roles;
import ru.nikidzawa.store.repositoreis.BooksRepository;
import ru.nikidzawa.store.repositoreis.ReadersRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Tag(name = "Читатели", description = "Управление читателями")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
public class ReadersController {

    ReadersRepository readersRepository;

    BookDtoFactory bookDtoFactory;

    ReaderDtoFactory readerDtoFactory;

    BooksRepository booksRepository;

    PasswordEncoder passwordEncoder;

    public static final String CREATE_READER = "/api/readers/registration";
    public static final String READERS_LIST = "api/readers";
    public static final String READER_INFO = "/api/readers/{readerId}";
    public static final String READER_BOOKS_LIST = "/api/readers/{readerId}/books";
    public static final String PATCH_READER = "/api/readers/{readerId}/edit";
    public static final String DELETE_READER = "/api/readers/{readerId}/delete";

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
    @ApiResponse (
            responseCode = "400",
            description = "Читатель с таким никнеймом уже существует",
            content = {
                    @Content (
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ReaderDto.class))
                    )
            })
    @PostMapping(CREATE_READER)
    public ReaderDto createReader (@RequestParam (value = "name") String name,
                                   @RequestParam (value = "nickname") String nickname,
                                   @RequestParam (value = "password") String password,
                                   @RequestParam (value = "mail") String mail) {
        readersRepository.findFirstByNickname(nickname)
                .ifPresent(existingReader -> {throw new BadRequestException("Читатель с таким именем уже существует");});

        return readerDtoFactory.createReader(readersRepository.saveAndFlush(
                ReaderEntity.builder()
                        .name(name)
                        .nickname(nickname)
                        .password(passwordEncoder.encode(password))
                        .mail(mail)
                        .role(Roles.READER)
                        .build()));
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
    @ApiResponse(
            responseCode = "401",
            description = "Не авторизован",
            content = {
                    @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Exception.class))
                    )
            })
    @PatchMapping(PATCH_READER)
    public ReaderDto editReader (@PathVariable (value = "readerId") Long id,
                                @RequestParam (value = "name", required = false) Optional <String> name,
                                @RequestParam (value = "nickname", required = false) Optional <String> nickname) {
        return readersRepository.findById(id).map(reader -> {
            boolean hasBeenEdited = false;
            if (name.isPresent()) {
                hasBeenEdited = true;
                reader.setName(name.get());
            }
            if (nickname.isPresent()) {
                hasBeenEdited = true;
                reader.setNickname(nickname.get());
            }
            if (hasBeenEdited) {
                return readerDtoFactory.createReader(readersRepository.saveAndFlush(reader));
            } else throw new BadRequestException("Данные не были изменены");
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
    @ApiResponse(
            responseCode = "401",
            description = "Не авторизован",
            content = {
                    @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Exception.class))
                    )
            })
    @GetMapping(READER_INFO)
    public ReaderDto readerInfo (@PathVariable Long readerId) {
        return readerDtoFactory.createReader(readersRepository.findById(readerId)
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
            description = "Cущности не найдены (будет указано)",
            content = {
                    @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Exception.class))
                    )}
    )
    @ApiResponse(
            responseCode = "401",
            description = "Не авторизован",
            content = {
                    @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Exception.class))
                    )
            })
    @GetMapping(READER_BOOKS_LIST)
    public List<BookDto> readerBooks (@PathVariable Long readerId) {
        return readersRepository.findById(readerId).map(reader -> {
            List<BookEntity> bookEntities = reader.getBooks();
            if (bookEntities.isEmpty()) {
                throw new NotFoundException("У читателя нет книг");
            } else {
                return bookEntities.stream()
                        .map(bookDtoFactory::createBook)
                        .collect(Collectors.toList());
            }
        }).orElseThrow(() -> new NotFoundException("Читателя не существует"));
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
    @ApiResponse(
            responseCode = "401",
            description = "Не авторизован или недостаточно прав",
            content = {
                    @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Exception.class))
                    )
            })
    @GetMapping(READERS_LIST)
    @PreAuthorize("hasAuthority('ADMIN')")
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
    @ApiResponse(
            responseCode = "401",
            description = "Не авторизован",
            content = {
                    @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Exception.class))
                    )
            })
    @DeleteMapping(DELETE_READER)
    @PreAuthorize("hasAuthority('ADMIN')")
    public OKResponse deleteReader(@PathVariable Long readerId) {
        return readersRepository.findById(readerId).map(reader -> {
            reader.getBooks().forEach(bookEntity -> {
                bookEntity.setReader(null);
                bookEntity.setDeadLine(null);
                bookEntity.setIssue(null);
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
