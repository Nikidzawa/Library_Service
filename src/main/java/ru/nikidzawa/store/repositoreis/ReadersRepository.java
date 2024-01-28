package ru.nikidzawa.store.repositoreis;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.nikidzawa.store.entities.ReaderEntity;

public interface ReadersRepository extends JpaRepository<ReaderEntity, Long> {
}
