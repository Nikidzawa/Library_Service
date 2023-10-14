package org.example.controllers.helpers;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.exceptions.NotFoundException;
import org.example.store.entities.ReaderEntity;
import org.example.store.repositoreis.ReadersRepository;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReaderSearchHelper {
    ReadersRepository readersRepository;
    public ReaderEntity SearchReader (Long id) {
        return readersRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }
}
