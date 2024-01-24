package ru.nikidzawa.controllers.helpers;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.nikidzawa.exceptions.NotFoundException;
import ru.nikidzawa.store.entities.BookEntity;
import ru.nikidzawa.store.repositoreis.BooksRepository;
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
