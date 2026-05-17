package com.henrique.ifconecta.infrastructure.web.academico.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.henrique.ifconecta.application.academico.dto.CriarTurmaInput;
import com.henrique.ifconecta.application.academico.dto.TurmaResumoDTO;
import com.henrique.ifconecta.application.academico.usecase.AprovarTurmaUseCase;
import com.henrique.ifconecta.application.academico.usecase.CancelarMatriculaUseCase;
import com.henrique.ifconecta.application.academico.usecase.CriarTurmaUseCase;
import com.henrique.ifconecta.application.academico.usecase.ListarMinhasTurmasUseCase;
import com.henrique.ifconecta.application.academico.usecase.ListarSolicitacoesTurmaParaProfessorUseCase;
import com.henrique.ifconecta.application.academico.usecase.ListarTurmasLecionadasUseCase;
import com.henrique.ifconecta.application.academico.usecase.ListarTurmasUseCase;
import com.henrique.ifconecta.application.academico.usecase.MatricularAlunoUseCase;
import com.henrique.ifconecta.application.academico.usecase.RejeitarTurmaUseCase;
import com.henrique.ifconecta.domain.shared.Pagina;
import com.henrique.ifconecta.infrastructure.config.security.CurrentUserId;
import com.henrique.ifconecta.infrastructure.web.academico.dto.CriarTurmaRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/turmas")
@RequiredArgsConstructor
public class TurmaController {

    private final CriarTurmaUseCase criarTurmaUseCase;
    private final MatricularAlunoUseCase matricularAlunoUseCase;
    private final ListarTurmasUseCase listarTurmasUseCase;
    private final ListarMinhasTurmasUseCase listarMinhasTurmasUseCase;
    private final ListarTurmasLecionadasUseCase listarTurmasLecionadasUseCase;
    private final CancelarMatriculaUseCase cancelarMatriculaUseCase;
    private final AprovarTurmaUseCase aprovarTurmaUseCase;
    private final RejeitarTurmaUseCase rejeitarTurmaUseCase;
    private final ListarSolicitacoesTurmaParaProfessorUseCase listarSolicitacoesTurmaParaProfessorUseCase;

    @PostMapping
    public ResponseEntity<Void> criarTurma(@RequestBody @Valid CriarTurmaRequest request,
            @CurrentUserId UUID solicitanteId) {
        criarTurmaUseCase.execute(new CriarTurmaInput(
                solicitanteId,
                request.disciplinaId(),
                request.professorId(),
                request.semestre(),
                request.codigoTurma()));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/solicitacoes-leciono")
    public ResponseEntity<List<TurmaResumoDTO>> listarSolicitacoesLeciono(@CurrentUserId UUID professorId) {
        return ResponseEntity.ok(listarSolicitacoesTurmaParaProfessorUseCase.execute(professorId));
    }

    @PostMapping("/{turmaId}/aprovar")
    public ResponseEntity<Void> aprovarTurma(@PathVariable UUID turmaId, @CurrentUserId UUID professorId) {
        aprovarTurmaUseCase.execute(turmaId, professorId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{turmaId}/rejeitar")
    public ResponseEntity<Void> rejeitarTurma(@PathVariable UUID turmaId, @CurrentUserId UUID professorId) {
        rejeitarTurmaUseCase.execute(turmaId, professorId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Pagina<TurmaResumoDTO>> listarTurmas(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "12") int tamanho,
            @RequestParam(required = false) UUID disciplinaId,
            @RequestParam(required = false) String semestre) {
        return ResponseEntity.ok(listarTurmasUseCase.execute(pagina, tamanho, disciplinaId, semestre));
    }

    @GetMapping("/minhas")
    public ResponseEntity<List<TurmaResumoDTO>> listarMinhasTurmas(@CurrentUserId UUID alunoId) {
        return ResponseEntity.ok(listarMinhasTurmasUseCase.execute(alunoId));
    }

    @GetMapping("/lecionadas")
    public ResponseEntity<List<TurmaResumoDTO>> listarLecionadas(@CurrentUserId UUID professorId) {
        return ResponseEntity.ok(listarTurmasLecionadasUseCase.execute(professorId));
    }

    @PostMapping("/{turmaId}/matricular")
    public ResponseEntity<Void> matricular(@PathVariable UUID turmaId, @CurrentUserId UUID alunoId) {
        matricularAlunoUseCase.execute(turmaId, alunoId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{turmaId}/matricula")
    public ResponseEntity<Void> cancelarMatricula(@PathVariable UUID turmaId, @CurrentUserId UUID alunoId) {
        cancelarMatriculaUseCase.execute(turmaId, alunoId);
        return ResponseEntity.noContent().build();
    }
}
