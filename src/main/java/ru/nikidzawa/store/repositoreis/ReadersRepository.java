package ru.nikidzawa.store.repositoreis;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.nikidzawa.store.entities.ReaderEntity;

import java.util.Optional;

public interface ReadersRepository extends JpaRepository<ReaderEntity, Long> {
    Optional<ReaderEntity> findFirstByNickname (String nickname);
}
