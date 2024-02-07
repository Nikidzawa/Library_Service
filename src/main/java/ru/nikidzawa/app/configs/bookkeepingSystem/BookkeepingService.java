package ru.nikidzawa.app.configs.bookkeepingSystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.nikidzawa.app.configs.bookkeepingSystem.mail.MailTaskDeque;
import ru.nikidzawa.app.store.entities.BookEntity;
import ru.nikidzawa.app.store.entities.ReaderEntity;

@Service
public class BookkeepingService {
    @Autowired
    BookkeepingFileController bookkeepingFileController;
    @Autowired
    MailTaskDeque mailTaskDeque;

    public void takeBook (long days, BookEntity book, ReaderEntity reader) {
        new Thread(() -> {
            mailTaskDeque.addMailTaskToDeque(days, book, reader);
            bookkeepingFileController.takeBook(book.getId(), reader);
        }).start();
    }

    public void handOverBook (long bookId) {
        new Thread(() -> {
            mailTaskDeque.shutdownSendMailTask(bookId);
            bookkeepingFileController.receiveBook(bookId);
        }).start();
    }
}
