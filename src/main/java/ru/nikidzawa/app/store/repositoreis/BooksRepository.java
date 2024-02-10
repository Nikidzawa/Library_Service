package ru.nikidzawa.app.store.repositoreis;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.nikidzawa.app.store.entities.BookEntity;

import java.util.Optional;

public interface BooksRepository extends JpaRepository <BookEntity, Long> {
    Optional<BookEntity> findFirstByName (String name);
}
