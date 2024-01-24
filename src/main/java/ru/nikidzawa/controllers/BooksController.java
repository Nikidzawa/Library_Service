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
import ru.nikidzawa.dto.BookDto;
import ru.nikidzawa.dto.ReaderDto;
import ru.nikidzawa.factory.BookDtoFactory;
import ru.nikidzawa.store.entities.BookEntity;
import ru.nikidzawa.store.entities.ReaderEntity;
import ru.nikidzawa.store.repositoreis.BooksRepository;
import org.springframework.web.bind.annotation.*;
import ru.nikidzawa.store.repositoreis.ReadersRepository;

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
    public static final String SET_OWNER = "api/books/set-owner/{book_id}";
    public static final String PATCH_BOOK = "api/books/edit/{id}";
    public static final String DELETE_BOOK = "api/books/delete/{id}";

    @Operation(summary = "Создать книгу")
    @PostMapping (CREATE_BOOK)
    public ResponseEntity<BookDto> createBook (@RequestParam (value = "name") String name,
                                               @RequestParam (value = "author") String author)
    {
        BookEntity bookEntity = booksRepository.saveAndFlush(
                BookEntity.builder()
                        .name(name)
                        .author(author)
                        .build());
        return ResponseEntity.status(HttpStatus.OK).body(bookDtoFactory.createBook(bookEntity));
    }

    @Operation(summary = "Присвоить книгу читателю")
    @ApiResponse(
            responseCode = "200",
            description = "Читатель книги изменён",
            content = {
                    @Content(
                            mediaType = "reader.json",
                            array = @ArraySchema(schema = @Schema(implementation = ReaderDto.class))
                    )
            })
    @ApiResponse (
            responseCode = "404",
            description = "Сущности не найдены",
            content = {
                    @Content(
                            mediaType = "string",
                            array = @ArraySchema(schema = @Schema(implementation = String.class))
                    )}
    )
    @PatchMapping(SET_OWNER)
    public ResponseEntity<?> assignReader (@PathVariable (value = "book_id") Long bookId,
                                           @RequestParam (value = "readerId") Optional<Long> readerId) {
        Optional<BookEntity> bookOptional = booksRepository.findById(bookId);

        if (bookOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Книга не найдена");
        }

        BookEntity bookEntity = bookOptional.get();

        if (readerId.isPresent()) {
            Optional<ReaderEntity> readerOptional = readersRepository.findById(readerId.get());

            if (readerOptional.isPresent()) {
                bookEntity.setReader(readerOptional.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Читатель не найден");
            }
        } else {
            bookEntity.setReader(null);
        }

        bookEntity = booksRepository.saveAndFlush(bookEntity);
        return ResponseEntity.status(HttpStatus.OK).body(bookDtoFactory.createBook(bookEntity));
    }


    @Operation(summary = "Изменить книгу")
    @ApiResponse(
            responseCode = "200",
            description = "Книга изменена",
            content = {
                    @Content(
                            mediaType = "reader.json",
                            array = @ArraySchema(schema = @Schema(implementation = ReaderDto.class))
                    )
            })
    @ApiResponse (
            responseCode = "400",
            description = "Ни один из параметров не был изменён",
            content = {
            @Content(
                    mediaType = "string",
                    array = @ArraySchema(schema = @Schema(implementation = String.class))
            )
    })
    @ApiResponse (
            responseCode = "404",
            description = "Книга не найдена",
            content = {
                    @Content(
                            mediaType = "string",
                            array = @ArraySchema(schema = @Schema(implementation = String.class))
                    )}
    )
    @PatchMapping(PATCH_BOOK)
    public ResponseEntity<?> patchBook (@PathVariable (value = "id") Long id,
                                        @RequestParam (value = "name", required = false) Optional <String> name,
                                        @RequestParam (value = "author", required = false) Optional <String> author)
    {
       Optional<BookEntity> optionalBook = booksRepository.findById(id);
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
            if (hasBeenEdited) {
                booksRepository.saveAndFlush(bookEntity);
                return ResponseEntity.status(HttpStatus.OK).body(bookDtoFactory.createBook(bookEntity));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ни один из параметров не был изменён");
       }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Книга не найдена"));
    }

    @Operation(summary = "Удалить книгу")
    @DeleteMapping(DELETE_BOOK)
    @ApiResponse(
            responseCode = "200",
            description = "Книга удалена",
            content = {
                    @Content(
                            mediaType = "reader.json",
                            array = @ArraySchema(schema = @Schema(implementation = ReaderDto.class))
                    )
            })
    @ApiResponse (
            responseCode = "404",
            description = "Книга не найдена",
            content = {
            @Content(
                    mediaType = "string",
                    array = @ArraySchema(schema = @Schema(implementation = String.class))
            )}
    )
    public ResponseEntity<String> deleteBook (@PathVariable (value = "id") Long id)
    {
        Optional<BookEntity> optionalBook = booksRepository.findById(id);
        if (optionalBook.isPresent()) {
            booksRepository.delete(optionalBook.get());
            return ResponseEntity.status(HttpStatus.OK).body("Книга удалена");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Книга не найдена");
    }
}
