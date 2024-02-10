package ru.nikidzawa.app.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.nikidzawa.app.services.RolesService;
import ru.nikidzawa.app.store.dto.ReaderDto;
import ru.nikidzawa.app.store.dto.factory.ReaderDtoFactory;

@Tag(name = "Роли", description = "Назначение ролей")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
public class RolesController {

    RolesService service;

    ReaderDtoFactory readerDtoFactory;

    private static final String setLibraryAdministratorRole = "api/readers/{nickname}/setLibraryAdministratorRole";
    private static final String setReaderRole = "api/readers/{nickname}/setReaderRole";

    @Operation(summary = "Выдать роль администратора (только для тестов)")
    @PatchMapping(setLibraryAdministratorRole)
    public ReaderDto setAdmRole (@PathVariable String nickname) {
        return readerDtoFactory.createReader(service.setAdmRole(nickname));
    }

    @Operation(summary = "Выдать роль читателя (только для тестов)")
    @PatchMapping(setReaderRole)
    public ReaderDto setReaderRole (@PathVariable String nickname) {
        return readerDtoFactory.createReader(service.setReaderRole(nickname));
    }
}
