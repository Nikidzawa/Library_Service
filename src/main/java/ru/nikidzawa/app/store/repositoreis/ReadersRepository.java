package ru.nikidzawa.app.store.repositoreis;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.nikidzawa.app.store.entities.ReaderEntity;

import java.util.Optional;

public interface ReadersRepository extends JpaRepository<ReaderEntity, Long> {
    Optional<ReaderEntity> findFirstByNickname (String nickname);
}
