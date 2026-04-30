package com.henrique.ifconecta.application.academico.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.henrique.ifconecta.domain.academico.model.Turma;
import com.henrique.ifconecta.domain.academico.port.TurmaRepository;
import com.henrique.ifconecta.domain.usuario.exception.NegocioException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MatricularAlunoUseCase {

    private final TurmaRepository turmaRepository;

    @Transactional
    public void execute(UUID turmaId, UUID alunoId) {
        Turma turma = turmaRepository.buscarPorId(turmaId)
                .orElseThrow(() -> new NegocioException("Turma não encontrada."));

        turma.matricularAluno(alunoId);

        turmaRepository.salvar(turma);
    }
}
