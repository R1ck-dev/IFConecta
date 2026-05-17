package com.henrique.ifconecta.application.academico.usecase;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.henrique.ifconecta.application.academico.dto.TurmaResumoDTO;
import com.henrique.ifconecta.domain.academico.port.TurmaRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ListarTurmasLecionadasUseCase {

    private final TurmaRepository turmaRepository;
    private final TurmaResumoMapper turmaResumoMapper;

    @Transactional
    public List<TurmaResumoDTO> execute(UUID professorId) {
        return turmaResumoMapper.montar(turmaRepository.listarLecionadasPorProfessor(professorId));
    }
}
