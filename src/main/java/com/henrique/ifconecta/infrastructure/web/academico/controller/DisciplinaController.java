package com.henrique.ifconecta.infrastructure.web.academico.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.henrique.ifconecta.application.academico.dto.DisciplinaResumoDTO;
import com.henrique.ifconecta.application.academico.usecase.ListarDisciplinasUseCase;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/disciplinas")
@RequiredArgsConstructor
public class DisciplinaController {

    private final ListarDisciplinasUseCase listarDisciplinasUseCase;

    @GetMapping
    public ResponseEntity<List<DisciplinaResumoDTO>> listarDisciplinas(
            @RequestParam(required = false) UUID cursoId) {
        return ResponseEntity.ok(listarDisciplinasUseCase.execute(cursoId));
    }
}
