package ru.nikidzawa.app.configs.bookkeepingSystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.nikidzawa.app.store.entities.ReaderEntity;

@Component
public class BookkeepingFileController {
    private final Logger logger = LoggerFactory.getLogger(BookkeepingFileController.class);

    public void takeBook (long bookId, ReaderEntity reader) {
        logger.info(String.format("ВЫДАЧА;BookId=%d;Email=%s;Nickname=%s;Name=%s",
                bookId, reader.getMail(), reader.getNickname(), reader.getName()));
    }
    public void receiveBook (long bookId) {
        logger.info("ВОЗВРАЩЕНИЕ;BookId=" + bookId);
    }
}
