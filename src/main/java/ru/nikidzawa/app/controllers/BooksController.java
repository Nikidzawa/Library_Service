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
import ru.nikidzawa.app.services.BookService;
import ru.nikidzawa.app.store.dto.BookDto;
import ru.nikidzawa.app.store.dto.factory.BookDtoFactory;

import java.util.List;
import java.util.Optional;

@Tag(name = "Книги", description = "Управление книгами")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
public class BooksController {

    BookDtoFactory bookDtoFactory;

    BookService service;

    public static final String CREATE_BOOK = "api/books/create";
    public static final String GET_BOOKS = "api/books";
    public static final String GET_BOOK = "api/books/{bookName}";
    public static final String PATCH_BOOK = "api/books/{bookName}/edit";
    public static final String SET_OWNER = "api/books/{bookName}/setOwner/{readerNickname}/{days}";
    public static final String REMOVE_OWNER = "api/books/{bookName}/removeOwner";
    public static final String DELETE_BOOK = "api/books/{bookName}/delete";

    @Operation(summary = "Создать книгу")
    @ApiResponse(
            responseCode = "200",
            description = "Книга создана",
            content = {
                    @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = BookDto.class))
                    )
            })
    @ApiResponse(
            responseCode = "401",
            description = "Не авторизован",
            content = {
                    @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Exception.class))
                    )
            })
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping (CREATE_BOOK)
    public BookDto createBook (@RequestParam (value = "name") String name,
                               @RequestParam (value = "author") String author,
                               @RequestParam (value = "description") String description) {
        return bookDtoFactory.createBook(service.createBook(name, author, description));
    }

    @Operation(summary = "Получить все книги")
    @ApiResponse(
            responseCode = "200",
            description = "Книги получены",
            content = {
                    @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = BookDto.class))
                    )
            })
    @ApiResponse (
            responseCode = "404",
            description = "Книги не найдены",
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
    @GetMapping(GET_BOOKS)
    public List<BookDto> allBooks () {
        return service.getAllBooks().stream()
                .map(bookDtoFactory::createBook)
                .toList();
    }

    @Operation(summary = "Получить книгу")
    @ApiResponse(
            responseCode = "200",
            description = "Книга получена",
            content = {
                    @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = BookDto.class))
                    )
            })
    @ApiResponse(
            responseCode = "404",
            description = "Книга не найдена",
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
    @GetMapping(GET_BOOK)
    public BookDto bookInfo (@PathVariable String bookName) {
        return bookDtoFactory.createBook(service.getBook(bookName));
    }

    @Operation(summary = "Изменить информацию о книге", description = "Укажите 0, если хотите убрать читателя")
    @ApiResponse(
            responseCode = "200",
            description = "Книга изменена",
            content = {
                    @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = BookDto.class))
                    )
            })
    @ApiResponse(
            responseCode = "400",
            description = "Ни один из параметров не был изменён",
            content = {
            @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = Exception.class))
            )
    })
    @ApiResponse(
            responseCode = "404",
            description = "Книга не найдена",
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
    @PatchMapping(PATCH_BOOK)
    @PreAuthorize("hasAuthority('ADMIN')")
    public BookDto editBook (@PathVariable String bookName,
                              @RequestParam (value = "name", required = false) Optional <String> name,
                              @RequestParam (value = "author", required = false) Optional <String> author,
                              @RequestParam (value = "description", required = false) Optional<String> description) {
       return bookDtoFactory.createBook(service.editBook(bookName, name, author, description));
    }

    @Operation(summary = "Выдать книгу читателю")
    @ApiResponse(
            responseCode = "200",
            description = "Книга выдана читателю",
            content = {
                    @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = BookDto.class))
                    )
            })
    @ApiResponse(
            responseCode = "404",
            description = "Указанной книги или читателя не существует (будет указано)",
            content = {
                    @Content (
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Exception.class))
                    )}
    )
    @ApiResponse(
            responseCode = "400",
            description = "У книги уже есть владелец",
            content = {
                    @Content (
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
    @PatchMapping(SET_OWNER)
    @PreAuthorize("hasAuthority('ADMIN')")
    public BookDto setOwner (@PathVariable String bookName,
                             @PathVariable String readerNickname,
                             @PathVariable Long days) {
        return bookDtoFactory.createBook(service.setOwner(bookName, readerNickname, days));
    }


    @Operation(summary = "Удалить владельца книги")
    @ApiResponse(
            responseCode = "200",
            description = "у книги больше нет владельца",
            content = {
                    @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = BookDto.class))
                    )
            })
    @ApiResponse(
            responseCode = "404",
            description = "Указанной книги не существует",
            content = {
                    @Content (
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Exception.class))
                    )}
    )
    @ApiResponse(
            responseCode = "400",
            description = "У книги нет владельца",
            content = {
                    @Content (
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
    @PatchMapping(REMOVE_OWNER)
    @PreAuthorize("hasAuthority('ADMIN')")
    public BookDto removeReader (@PathVariable String bookName) {
        return bookDtoFactory.createBook (service.removeOwner(bookName));
    }

    @Operation(summary = "Удалить книгу")
    @ApiResponse(
            responseCode = "200",
            description = "Книга удалена из базы данных",
            content = {
                    @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = OKResponse.class))
                    )
            })
    @ApiResponse(
            responseCode = "404",
            description = "Указанной книги не существует",
            content = {
            @Content (
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
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(DELETE_BOOK)
    public OKResponse deleteBook (@PathVariable (value = "bookName") String bookName) {
        return service.deleteBook(bookName);
    }
}
