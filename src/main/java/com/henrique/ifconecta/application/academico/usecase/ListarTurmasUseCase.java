package com.henrique.ifconecta.application.academico.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.henrique.ifconecta.application.academico.dto.TurmaResumoDTO;
import com.henrique.ifconecta.domain.academico.model.Turma;
import com.henrique.ifconecta.domain.academico.port.TurmaRepository;
import com.henrique.ifconecta.domain.shared.Pagina;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ListarTurmasUseCase {

    private final TurmaRepository turmaRepository;
    private final TurmaResumoMapper turmaResumoMapper;

    @Transactional
    public Pagina<TurmaResumoDTO> execute(int pagina, int tamanho, UUID disciplinaId, String semestre) {
        Pagina<Turma> paginaTurmas = turmaRepository.listarAtivas(pagina, tamanho, disciplinaId, semestre);
        var resumos = turmaResumoMapper.montar(paginaTurmas.itens());
        return new Pagina<>(resumos, paginaTurmas.paginaAtual(), paginaTurmas.totalPaginas(), paginaTurmas.totalItens());
    }
}
