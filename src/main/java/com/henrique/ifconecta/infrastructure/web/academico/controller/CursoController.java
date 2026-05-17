package com.henrique.ifconecta.infrastructure.web.academico.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.henrique.ifconecta.application.academico.dto.CursoResumoDTO;
import com.henrique.ifconecta.application.academico.usecase.ListarCursosUseCase;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cursos")
@RequiredArgsConstructor
public class CursoController {

    private final ListarCursosUseCase listarCursosUseCase;

    @GetMapping
    public ResponseEntity<List<CursoResumoDTO>> listarCursos() {
        return ResponseEntity.ok(listarCursosUseCase.execute());
    }
}
