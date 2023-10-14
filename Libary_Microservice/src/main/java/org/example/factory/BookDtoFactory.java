package org.example.factory;

import org.example.dto.BookDto;
import org.example.store.entities.BookEntity;
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
