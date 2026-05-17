package com.henrique.ifconecta.application.academico.usecase;

import java.util.List;

import org.springframework.stereotype.Service;

import com.henrique.ifconecta.application.academico.dto.CursoResumoDTO;
import com.henrique.ifconecta.domain.academico.port.CursoRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ListarCursosUseCase {

    private final CursoRepository cursoRepository;

    @Transactional
    public List<CursoResumoDTO> execute() {
        return cursoRepository.listarTodos().stream()
                .map(curso -> new CursoResumoDTO(
                        curso.getId(),
                        curso.getNome(),
                        curso.getSigla(),
                        curso.getModalidade()))
                .toList();
    }
}
