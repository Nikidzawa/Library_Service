package org.example.store.repositoreis;

import org.example.store.entities.ReaderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReadersRepository extends JpaRepository<ReaderEntity, Long> {
}
