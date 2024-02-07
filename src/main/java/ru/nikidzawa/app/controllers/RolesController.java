package ru.nikidzawa.app.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.nikidzawa.app.dto.ReaderDto;
import ru.nikidzawa.app.store.entities.ReaderEntity;
import ru.nikidzawa.app.store.entities.Roles;
import ru.nikidzawa.app.store.repositoreis.ReadersRepository;
import ru.nikidzawa.app.dto.factory.ReaderDtoFactory;
import ru.nikidzawa.app.responses.exceptions.NotFoundException;

@Tag(name = "Роли", description = "Назначение ролей")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
public class RolesController {

    ReadersRepository readersRepository;

    ReaderDtoFactory readerDtoFactory;

    private static final String setLibraryAdministratorRole = "api/readers/{nickname}/setLibraryAdministratorRole";
    private static final String setReaderRole = "api/readers/{nickname}/setReaderRole";

    @Operation(summary = "Выдать роль администратора (только для тестов)")
    @PatchMapping(setLibraryAdministratorRole)
    public ReaderDto setAdmRole (@PathVariable String nickname) {
        ReaderEntity reader = readersRepository.findFirstByNickname(nickname)
                .orElseThrow(() -> new NotFoundException("Пользователя не существует"));
        reader.setRole(Roles.ADMIN);
        return readerDtoFactory.createReader(readersRepository.saveAndFlush(reader));
    }

    @Operation(summary = "Выдать роль читателя (только для тестов)")
    @PatchMapping(setReaderRole)
    public ReaderDto setReaderRole (@PathVariable String nickname) {
        ReaderEntity reader = readersRepository.findFirstByNickname(nickname)
                .orElseThrow(() -> new NotFoundException("Пользователя не существует"));
        reader.setRole(Roles.READER);
        return readerDtoFactory.createReader(readersRepository.saveAndFlush(reader));
    }
}
