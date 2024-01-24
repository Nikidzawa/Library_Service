package ru.nikidzawa.controllers;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.nikidzawa.controllers.helpers.BookSearchHelper;
import ru.nikidzawa.controllers.helpers.ReaderSearchHelper;
import ru.nikidzawa.dto.BookDto;
import ru.nikidzawa.factory.BookDtoFactory;
import ru.nikidzawa.store.entities.BookEntity;
import ru.nikidzawa.store.entities.ReaderEntity;
import ru.nikidzawa.store.repositoreis.BooksRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@FieldDefaults (level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
public class BooksController {
    BooksRepository booksRepository;
    BookDtoFactory bookDtoFactory;
    BookSearchHelper bookSearchHelper;
    ReaderSearchHelper readerSearchHelper;

    public static final String CREATE_BOOK = "api/book/create";

    public static final String PATCH_BOOK = "api/book/{id}";
    public static final String DELETE_BOOK = "api/book/{id}";
    public static final String REASSIGN_BOOK = "api/reader/{id_book}&{id_reader}";

    @PostMapping (CREATE_BOOK)
    public BookDto createBook (@RequestParam (value = "name") String name,
                               @RequestParam (value = "author") String author,
                               @RequestParam (value = "reader") Optional <Long> id)
    {
        final BookEntity bookEntity = BookEntity.builder().name(name).author(author).build();

        id.ifPresent(readerId -> {
            bookEntity.setReader(readerSearchHelper.SearchReader(readerId));
        });

    final BookEntity creatredBook = booksRepository.saveAndFlush(bookEntity);
    return bookDtoFactory.createBook(creatredBook);
    }

    @PatchMapping(PATCH_BOOK)
    public BookDto patchBook (@PathVariable (value = "id") Optional <Long> id,
                              @RequestParam (value = "name") Optional <String> name,
                              @RequestParam (value = "author") Optional <String> author)
    {
       final BookEntity bookEntity = id
               .map(bookSearchHelper::SearchBook)
               .orElse(BookEntity.builder().build());
       name.ifPresent(bookEntity::setName);
       author.ifPresent(bookEntity::setAuthor);

       final BookEntity bookCreated = booksRepository.saveAndFlush(bookEntity);
       return bookDtoFactory.createBook(bookCreated);
    }

    @DeleteMapping(DELETE_BOOK)
    public boolean deleteBook (@PathVariable (value = "id") Long id)
    {
        BookEntity book = bookSearchHelper.SearchBook(id);
        booksRepository.delete(book);
        return true;
    }

    @PatchMapping(REASSIGN_BOOK)
    public BookDto reassignBOOK(@PathVariable (value = "id_book") Long idBook,
                                @PathVariable (value = "id_reader") Long idReader)
    {
        BookEntity book = bookSearchHelper.SearchBook(idBook);
        ReaderEntity reader = readerSearchHelper.SearchReader(idReader);
        book.setReader(reader);
        final BookEntity createdBook = booksRepository.saveAndFlush(book);
        return bookDtoFactory.createBook(createdBook);
    }
}
