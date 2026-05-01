package com.henrique.ifconecta.infrastructure.web.academico.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.henrique.ifconecta.application.academico.dto.CriarCursoInput;
import com.henrique.ifconecta.application.academico.usecase.CriarCursoComDisciplinasUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/academico")
@RequiredArgsConstructor
@Tag(name = "Admin - Acadêmico", description = "Operações administrativas e setup acadêmico")
public class AdminAcademicoSeederController {
    private final CriarCursoComDisciplinasUseCase criarCursoComDisciplinasUseCase;

    @Operation(summary = "Semear Banco", description = "Cadastra um curso com suas respectivas disciplinas.")
    @ApiResponse(responseCode = "201", description = "Curso e disciplinas cadastrados")
    @PostMapping("/seeder")
    public ResponseEntity<Void> semearBanco(@RequestBody CriarCursoInput request) {
        criarCursoComDisciplinasUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
