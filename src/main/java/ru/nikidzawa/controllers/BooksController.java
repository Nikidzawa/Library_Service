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
import ru.nikidzawa.dto.factory.BookDtoFactory;
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

@Tag(name = "Книги", description = "Управление книгами")
@RequiredArgsConstructor
@Transactional
@FieldDefaults (level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
public class BooksController {
    BooksRepository booksRepository;
    ReadersRepository readersRepository;
    BookDtoFactory bookDtoFactory;

    public static final String CREATE_BOOK = "api/books/create";
    public static final String GET_BOOKS = "api/books";
    public static final String GET_BOOK = "api/books/{id}";
    public static final String PATCH_BOOK = "api/books/edit/{bookId}";
    public static final String DELETE_BOOK = "api/books/delete/{id}";

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
    @PostMapping (CREATE_BOOK)
    public BookDto createBook (@RequestParam (value = "name") String name,
                               @RequestParam (value = "author") String author) {
        return bookDtoFactory.createBook(booksRepository.saveAndFlush(
                BookEntity.builder()
                        .name(name)
                        .author(author)
                        .reader(null)
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
    @ApiResponse (
            responseCode = "404",
            description = "Книга не найдена",
            content = {
                    @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Exception.class))
                    )}
    )
    @GetMapping(GET_BOOK)
    public BookDto bookInfo (@PathVariable (value = "id") Long id) {
        return bookDtoFactory.createBook(booksRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Книга не найдена")));
    }

    @Operation(summary = "Изменить книгу", description = "Укажите 0, если хотите убрать читателя")
    @ApiResponse(
            responseCode = "200",
            description = "Книга изменена",
            content = {
                    @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = BookDto.class))
                    )
            })
    @ApiResponse (
            responseCode = "400",
            description = "Ни один из параметров не был изменён",
            content = {
            @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = Exception.class))
            )
    })
    @ApiResponse (
            responseCode = "404",
            description = "Сущности не найдены",
            content = {
                    @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Exception.class))
                    )}
    )
    @PatchMapping(PATCH_BOOK)
    public BookDto editeBook (@PathVariable (value = "bookId") Long bookId,
                              @RequestParam (value = "name", required = false) Optional <String> name,
                              @RequestParam (value = "author", required = false) Optional <String> author,
                              @RequestParam (value = "readerId", required = false) Optional<Long> readerId) {
       Optional<BookEntity> optionalBook = booksRepository.findById(bookId);
       return optionalBook.map(bookEntity -> {
            boolean hasBeenEdited = false;
            if (name.isPresent()) {
                hasBeenEdited = true;
                bookEntity.setName(name.get());
            }
            if (author.isPresent()) {
                hasBeenEdited = true;
                bookEntity.setAuthor(author.get());
            }
            if (readerId.isPresent()) {
                hasBeenEdited = true;
                bookEntity.setReader(readerId.map(id -> readersRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("читатель не найден")))
                        .orElse(null));
            }
            if (hasBeenEdited) {
                booksRepository.saveAndFlush(bookEntity);
                return bookDtoFactory.createBook(bookEntity);
            }
            throw new BadRequestException("Ни один из параметров не был изменён");
       }).orElseThrow(() -> new NotFoundException("Книга не найдена"));
    }

    @Operation(summary = "Удалить книгу")
    @DeleteMapping(DELETE_BOOK)
    @ApiResponse(
            responseCode = "200",
            description = "Книга удалена из базы данных",
            content = {
                    @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = OKResponse.class))
                    )
            })
    @ApiResponse (
            responseCode = "404",
            description = "Указанной книги не существует",
            content = {
            @Content (
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = Exception.class))
            )}
    )
    public OKResponse deleteBook (@PathVariable (value = "id") Long id) {
        Optional<BookEntity> bookOptional = booksRepository.findById(id);

        if (bookOptional.isPresent()) {
            BookEntity bookEntity = bookOptional.get();
            ReaderEntity reader = bookEntity.getReader();
            reader.getBooks().remove(bookEntity);
            readersRepository.saveAndFlush(reader);
            booksRepository.delete(bookEntity);
            return OKResponse.builder()
                    .code(200)
                    .message("Книга удалена из базы данных")
                    .build();
        } else {
            throw new NotFoundException("Указанной книги не существует");
        }
    }
}
