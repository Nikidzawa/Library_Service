package ru.nikidzawa.factory;

import ru.nikidzawa.dto.BookDto;
import ru.nikidzawa.store.entities.BookEntity;
import org.springframework.stereotype.Component;

@Component
public class BookDtoFactory {
    public BookDto createBook (BookEntity book) {
        return BookDto.builder()
                .id(book.getId())
                .name(book.getName())
                .author(book.getAuthor())
                .build();
    }
}
