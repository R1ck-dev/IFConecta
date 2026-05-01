package com.henrique.ifconecta.infrastructure.web.usuario.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.henrique.ifconecta.application.usuario.dto.ConvidarInstitucionalInput;
import com.henrique.ifconecta.application.usuario.dto.ConvidarProfessorInput;
import com.henrique.ifconecta.application.usuario.usecase.ConvidarInstitucionalUseCase;
import com.henrique.ifconecta.application.usuario.usecase.ConvidarProfessorUseCase;
import com.henrique.ifconecta.infrastructure.web.usuario.dto.ConvidarInstitucionalRequest;
import com.henrique.ifconecta.infrastructure.web.usuario.dto.ConvidarProfessorRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/usuarios")
@RequiredArgsConstructor
public class AdminUsuarioController {

    private final ConvidarProfessorUseCase convidarProfessorUseCase;
    private final ConvidarInstitucionalUseCase convidarInstitucionalUseCase;

    @PostMapping("/professores/convidar")
    public ResponseEntity<Void> convidarProfessor(@RequestBody @Valid ConvidarProfessorRequest request) {
        convidarProfessorUseCase.execute(new ConvidarProfessorInput(
                request.nome(),
                request.emailAcad(),
                request.siape()));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/institucionais/convidar")
    public ResponseEntity<Void> convidarInstitucional(@RequestBody @Valid ConvidarInstitucionalRequest request) {
        convidarInstitucionalUseCase.execute(new ConvidarInstitucionalInput(
                request.nome(),
                request.emailAcad(),
                request.setor(),
                request.cargo()));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
