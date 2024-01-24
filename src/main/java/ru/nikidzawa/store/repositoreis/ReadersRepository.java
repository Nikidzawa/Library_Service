package ru.nikidzawa.store.repositoreis;

import ru.nikidzawa.store.entities.ReaderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReadersRepository extends JpaRepository<ReaderEntity, Long> {
}
