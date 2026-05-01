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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/turmas")
@RequiredArgsConstructor
@Tag(name = "Turmas", description = "Gestão de turmas acadêmicas e matrículas")
public class TurmaController {

    private final CriarTurmaUseCase criarTurmaUseCase;
    private final MatricularAlunoUseCase matricularAlunoUseCase;

    @Operation(summary = "Criar Turma", description = "Cria uma nova turma vinculada a uma disciplina e a um professor.")
    @ApiResponse(responseCode = "201", description = "Turma criada com sucesso")
    @PostMapping
    public ResponseEntity<Void> criarTurma(@RequestBody @Valid CriarTurmaRequest request) {
        
        criarTurmaUseCase.execute(new CriarTurmaInput(
                request.disciplinaId(),
                request.professorId(),
                request.semestre(),
                request.codigoTurma()));

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Matricular Aluno", description = "Matricula o aluno logado em uma turma específica.")
    @ApiResponse(responseCode = "200", description = "Matrícula realizada com sucesso")
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
