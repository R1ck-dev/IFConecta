package com.henrique.ifconecta.infrastructure.web.academico.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.henrique.ifconecta.application.academico.dto.CriarCursoInput;
import com.henrique.ifconecta.application.academico.usecase.CriarCursoComDisciplinasUseCase;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/academico")
@RequiredArgsConstructor
public class AcademicoSeederController {
    private final CriarCursoComDisciplinasUseCase criarCursoComDisciplinasUseCase;

    // TODO: Proteger rota para perfis de ADMIN
    @PostMapping("/seeder")
    public ResponseEntity<Void> semearBanco(@RequestBody CriarCursoInput request) {
        criarCursoComDisciplinasUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
