package ru.nikidzawa.store.repositoreis;

import ru.nikidzawa.store.entities.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BooksRepository extends JpaRepository <BookEntity, Long> {
    List<BookEntity> findAllByReader_Id (Long id);
}
