package ru.nikidzawa.app.controllers;

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
import org.springframework.web.bind.annotation.*;
import ru.nikidzawa.app.responses.OKResponse;
import ru.nikidzawa.app.responses.exceptions.Exception;
import ru.nikidzawa.app.services.ReaderService;
import ru.nikidzawa.app.store.dto.BookDto;
import ru.nikidzawa.app.store.dto.ReaderDto;
import ru.nikidzawa.app.store.dto.factory.BookDtoFactory;
import ru.nikidzawa.app.store.dto.factory.ReaderDtoFactory;

import java.util.List;
import java.util.Optional;

@Tag(name = "Читатели", description = "Управление читателями")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
public class ReadersController {

    BookDtoFactory bookDtoFactory;

    ReaderDtoFactory readerDtoFactory;

    ReaderService service;

    public static final String CREATE_READER = "/api/readers/registration";
    public static final String READERS_LIST = "api/readers";
    public static final String READER_INFO = "/api/readers/{readerNickname}";
    public static final String READER_BOOKS_LIST = "/api/readers/{readerNickname}/books";
    public static final String PATCH_READER = "/api/readers/{readerNickname}/edit";
    public static final String DELETE_READER = "/api/readers/{readerNickname}/delete";

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
        return readerDtoFactory.createReader(service.createReader(name, nickname, password, mail));
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
    public ReaderDto editReader (@PathVariable (value = "readerNickname") String readerNickname,
                                 @RequestParam (value = "name", required = false) Optional <String> name,
                                 @RequestParam (value = "nickname", required = false) Optional <String> nickname) {
        return readerDtoFactory.createReader(service.editReader(readerNickname, nickname, name));
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
    public ReaderDto readerInfo (@PathVariable String readerNickname) {
        return readerDtoFactory.createReader(service.getReader(readerNickname));
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
    public List<BookDto> readerBooks (@PathVariable String readerNickname) {
        return service.getReaderBooks(readerNickname).stream()
                .map(bookDtoFactory::createBook).toList();
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
    public List<ReaderDto> getAllReaders () {
        return service.getAllReaders().stream()
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
    public OKResponse deleteReader(@PathVariable String readerNickname) {
        return service.deleteReader(readerNickname);
    }
}
