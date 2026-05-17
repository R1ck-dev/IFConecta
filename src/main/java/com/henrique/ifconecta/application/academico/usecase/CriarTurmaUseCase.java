package com.henrique.ifconecta.application.academico.usecase;

import org.springframework.stereotype.Service;

import com.henrique.ifconecta.application.academico.dto.CriarTurmaInput;
import com.henrique.ifconecta.domain.academico.enums.StatusTurma;
import com.henrique.ifconecta.domain.academico.model.Turma;
import com.henrique.ifconecta.domain.academico.port.TurmaRepository;
import com.henrique.ifconecta.domain.usuario.exception.NegocioException;
import com.henrique.ifconecta.domain.usuario.model.Aluno;
import com.henrique.ifconecta.domain.usuario.model.Professor;
import com.henrique.ifconecta.domain.usuario.model.Usuario;
import com.henrique.ifconecta.domain.usuario.port.UsuarioRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CriarTurmaUseCase {

    private final TurmaRepository turmaRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public void execute(CriarTurmaInput input) {
        Usuario solicitante = usuarioRepository.buscarPorId(input.solicitanteId())
                .orElseThrow(() -> new NegocioException("Solicitante nao encontrado."));

        Usuario professor = usuarioRepository.buscarPorId(input.professorId())
                .orElseThrow(() -> new NegocioException("Professor nao encontrado."));

        if (!(professor instanceof Professor)) {
            throw new NegocioException("O usuario selecionado para responsavel da turma deve ser um professor.");
        }

        StatusTurma status = resolverStatusInicial(solicitante, input.professorId());

        Turma novaTurma = new Turma(
                input.disciplinaId(),
                input.professorId(),
                input.semestre(),
                input.codigoTurma(),
                status,
                input.solicitanteId()
        );

        turmaRepository.salvar(novaTurma);
    }

    private StatusTurma resolverStatusInicial(Usuario solicitante, java.util.UUID professorId) {
        if (solicitante instanceof Aluno) {
            return StatusTurma.PENDENTE;
        }
        if (solicitante instanceof Professor && !solicitante.getId().equals(professorId)) {
            throw new NegocioException("Professor so pode criar turma para si mesmo.");
        }
        return StatusTurma.ATIVA;
    }
}
