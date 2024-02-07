package ru.nikidzawa.app.configs.bookkeepingSystem.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import ru.nikidzawa.app.store.entities.BookEntity;
import ru.nikidzawa.app.store.entities.ReaderEntity;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class MailTaskDeque {
    private final HashMap<Long, ScheduledExecutorService> dequeEmailMessages = new HashMap<>();
    @Autowired
    private JavaMailSender javaMailSender;

    public void addMailTaskToDeque (long days, BookEntity book, ReaderEntity reader) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        Long bookId = book.getId();
        scheduler.schedule(() -> {
            sendMessage(book, reader);
            shutdownSendMailTask(bookId);
        }, days, TimeUnit.DAYS);
        dequeEmailMessages.put(bookId, scheduler);
    }

    public void shutdownSendMailTask (long bookId) {
        ScheduledExecutorService scheduler = dequeEmailMessages.get(bookId);
        if (scheduler != null && !scheduler.isShutdown()) {scheduler.shutdownNow();}
        dequeEmailMessages.remove(bookId);
    }

    private void sendMessage(BookEntity book, ReaderEntity reader) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("nikidzawa@mail.ru");
        message.setTo(reader.getMail());
        message.setSubject("Необходимо вернуть книгу!");
        message.setText(String.format("Здравствуйте, %s, %s числа вы взяли книгу - \"%s\". Сегодня её необходимо вернуть в библиотеку",
                reader.getName(), book.getIssue().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")), book.getName()));
        javaMailSender.send(message);
    }
}
