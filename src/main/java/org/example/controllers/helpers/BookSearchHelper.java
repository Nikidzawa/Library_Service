package org.example.controllers.helpers;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.exceptions.NotFoundException;
import org.example.store.entities.BookEntity;
import org.example.store.repositoreis.BooksRepository;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookSearchHelper {
    BooksRepository booksRepository;
    public BookEntity SearchBook (Long id) {
        return booksRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Книга не найдена"));
    }
}
