package com.henrique.ifconecta.infrastructure.web.academico.Turma.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.henrique.ifconecta.application.academico.dto.CriarTurmaInput;
import com.henrique.ifconecta.application.academico.usecase.CriarTurmaUseCase;
import com.henrique.ifconecta.application.academico.usecase.MatricularAlunoUseCase;
import com.henrique.ifconecta.infrastructure.web.academico.Turma.dto.CriarTurmaRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/turmas")
@RequiredArgsConstructor
public class TurmaController {

    private final CriarTurmaUseCase criarTurmaUseCase;
    private final MatricularAlunoUseCase matricularAlunoUseCase;

    @PostMapping
    public ResponseEntity<Void> criarTurma(@RequestBody @Valid CriarTurmaRequest request) {
        
        criarTurmaUseCase.execute(new CriarTurmaInput(
                request.disciplinaId(),
                request.professorId(),
                request.semestre(),
                request.codigoTurma()));

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/{turmaId}/matricular")
    public ResponseEntity<Void> matricular(@PathVariable UUID turmaId) {
        String alunoId = extraiId();

        matricularAlunoUseCase.execute(turmaId, UUID.fromString(alunoId));
        return ResponseEntity.ok().build();
    }

    private String extraiId() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
