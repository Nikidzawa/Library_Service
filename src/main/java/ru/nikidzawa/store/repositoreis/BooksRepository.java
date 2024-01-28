package ru.nikidzawa.store.repositoreis;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.nikidzawa.store.entities.BookEntity;

public interface BooksRepository extends JpaRepository <BookEntity, Long> {
}
