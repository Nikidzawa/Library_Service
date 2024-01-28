package ru.nikidzawa.dto.factory;

import org.springframework.stereotype.Component;
import ru.nikidzawa.dto.BookDto;
import ru.nikidzawa.store.entities.BookEntity;

@Component
public class BookDtoFactory {
    public BookDto createBook (BookEntity book) {
        return BookDto.builder()
                .id(book.getId())
                .name(book.getName())
                .author(book.getAuthor())
                .ownerID(book.getReader() == null ? null : book.getReader().getId())
                .build();
    }
}
