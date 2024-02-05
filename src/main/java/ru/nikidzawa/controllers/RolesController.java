package ru.nikidzawa.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.nikidzawa.responses.exceptions.NotFoundException;
import ru.nikidzawa.store.entities.ReaderEntity;
import ru.nikidzawa.store.entities.Roles;
import ru.nikidzawa.store.repositoreis.ReadersRepository;

@Tag(name = "Роли", description = "Назначение ролей")
@RequiredArgsConstructor
@RestController
public class RolesController {

    ReadersRepository readersRepository;

    private static final String setLibraryAdministratorRole = "api/readers/{nickname}/setLibraryAdministratorRole";
    private static final String setReaderRole = "api/readers/{nickname}/setReaderRole";

    @Operation(summary = "Выдать роль администратора (только для тестов)")
    @PatchMapping(setLibraryAdministratorRole)
    public ReaderEntity setAdmRole (@PathVariable (value = "nickname") String nickname) {
        ReaderEntity reader = readersRepository.findFirstByNickname(nickname)
                .orElseThrow(() -> new NotFoundException("Пользователя не существует"));
        reader.setRole(Roles.ADMIN);
        return readersRepository.saveAndFlush(reader);
    }

    @Operation(summary = "Выдать роль читателя (только для тестов)")
    @PatchMapping(setReaderRole)
    public ReaderEntity setReaderRole (@PathVariable (value = "nickname") String nickname) {
        ReaderEntity reader = readersRepository.findFirstByNickname(nickname)
                .orElseThrow(() -> new NotFoundException("Пользователя не существует"));
        reader.setRole(Roles.READER);
        return readersRepository.saveAndFlush(reader);
    }
}
