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
import org.springframework.web.bind.annotation.*;
import ru.nikidzawa.configs.bookkeepingSystem.BookkeepingService;
import ru.nikidzawa.dto.BookDto;
import ru.nikidzawa.dto.factory.BookDtoFactory;
import ru.nikidzawa.responses.OKResponse;
import ru.nikidzawa.responses.exceptions.BadRequestException;
import ru.nikidzawa.responses.exceptions.Exception;
import ru.nikidzawa.responses.exceptions.NotFoundException;
import ru.nikidzawa.store.entities.BookEntity;
import ru.nikidzawa.store.entities.ReaderEntity;
import ru.nikidzawa.store.repositoreis.BooksRepository;
import ru.nikidzawa.store.repositoreis.ReadersRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Tag(name = "Книги", description = "Управление книгами")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
public class BooksController {

    BooksRepository booksRepository;

    ReadersRepository readersRepository;

    BookDtoFactory bookDtoFactory;

    BookkeepingService bookkeepingService;

    public static final String CREATE_BOOK = "api/books/create";
    public static final String GET_BOOKS = "api/books";
    public static final String GET_BOOK = "api/books/{bookId}";
    public static final String PATCH_BOOK = "api/books/{bookId}/edit";
    public static final String SET_OWNER = "api/books/{bookId}/setOwner/{readerNickname}/{days}";
    public static final String REMOVE_OWNER = "api/books/{bookId}/removeOwner";
    public static final String DELETE_BOOK = "api/books/{bookId}/delete";

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
        return bookDtoFactory.createBook(booksRepository.saveAndFlush(
                BookEntity.builder()
                        .name(name)
                        .author(author)
                        .description(description)
                        .build()
                )
        );
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
        List<BookEntity> bookEntities = booksRepository.findAll();
        if (bookEntities.isEmpty()) {
            throw new NotFoundException("Книги не найдены");
        }
        return bookEntities.stream()
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
    public BookDto bookInfo (@PathVariable Long bookId) {
        return bookDtoFactory.createBook(booksRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException("Книга не найдена")));
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
    public BookDto editeBook (@PathVariable Long bookId,
                              @RequestParam (value = "name", required = false) Optional <String> name,
                              @RequestParam (value = "author", required = false) Optional <String> author,
                              @RequestParam (value = "description", required = false) Optional<String> description) {
       return booksRepository.findById(bookId).map(bookEntity -> {
            boolean hasBeenEdited = false;
            if (name.isPresent()) {
                hasBeenEdited = true;
                bookEntity.setName(name.get());
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
                return bookDtoFactory.createBook(bookEntity);
            } else throw new BadRequestException("Ни один из параметров не был изменён");
       }).orElseThrow(() -> new NotFoundException("Книга не найдена"));
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
    public BookDto setOwner (@PathVariable Long bookId,
                             @PathVariable String readerNickname,
                             @PathVariable Long days) {
        ReaderEntity reader = readersRepository.findFirstByNickname(readerNickname)
                .orElseThrow(() -> new NotFoundException("Пользователя с таким именем не существует"));
        BookEntity book = booksRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException("Книги с указанным id не существует"));
        if (book.getReader() != null) {throw new BadRequestException("У книги уже есть владелец");}

        reader.getBooks().add(book);
        book.setReader(reader);
        book.setIssue(LocalDateTime.now());
        book.setDeadLine(LocalDateTime.now().plusDays(days));
        bookkeepingService.takeBook(days, book, reader);

        readersRepository.saveAndFlush(reader);
        return bookDtoFactory.createBook(booksRepository.saveAndFlush(book));
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
    public BookDto removeReader (@PathVariable Long bookId) {
        return bookDtoFactory.createBook (
                booksRepository.saveAndFlush(
                        booksRepository.findById(bookId)
                                .map(bookEntity -> {
                                    if (bookEntity.getReader() == null) {
                                        throw new BadRequestException("У книги нет владельца");
                                    }
                                    bookEntity.setReader(null);
                                    bookEntity.setIssue(null);
                                    bookEntity.setDeadLine(null);
                                    bookkeepingService.handOverBook(bookId);
                                    return bookEntity;
                                }).orElseThrow(() -> new NotFoundException("Книги не существует"))
                )
        );
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
    public OKResponse deleteBook (@PathVariable (value = "bookId") Long id) {
        return booksRepository.findById(id).map(bookEntity -> {
            ReaderEntity reader = bookEntity.getReader();
            if (reader != null) {
                reader.getBooks().remove(bookEntity);
                readersRepository.saveAndFlush(reader);
            }
            booksRepository.delete(bookEntity);
            return OKResponse.builder()
                    .code(200)
                    .message("Книга удалена из базы данных")
                    .build();
        }).orElseThrow(() -> new NotFoundException("Указанной книги не существует"));
    }
}
