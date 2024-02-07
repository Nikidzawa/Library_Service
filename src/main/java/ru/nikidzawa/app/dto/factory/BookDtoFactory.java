package ru.nikidzawa.app.dto.factory;

import org.springframework.stereotype.Component;
import ru.nikidzawa.app.dto.BookDto;
import ru.nikidzawa.app.store.entities.BookEntity;

@Component
public class BookDtoFactory {
    public BookDto createBook (BookEntity book) {
        BookDto bookDto = BookDto.builder()
                .id(book.getId())
                .name(book.getName())
                .author(book.getAuthor())
                .description(book.getDescription())
                .build();
        if (book.getReader() != null) {
            bookDto.setIssue(book.getIssue());
            bookDto.setDeadLine(book.getDeadLine());
            bookDto.setOwner(book.getReader().getNickname());
        }
        return bookDto;
    }
}
