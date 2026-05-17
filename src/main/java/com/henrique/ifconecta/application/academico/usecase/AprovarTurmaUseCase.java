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
public class AprovarTurmaUseCase {

    private final TurmaRepository turmaRepository;

    @Transactional
    public void execute(UUID turmaId, UUID professorId) {
        Turma turma = turmaRepository.buscarPorId(turmaId)
                .orElseThrow(() -> new NegocioException("Turma nao encontrada."));
        turma.aprovar(professorId);
        turmaRepository.salvar(turma);
    }
}
