package com.henrique.ifconecta.application.academico.usecase;

import org.springframework.stereotype.Service;

import com.henrique.ifconecta.application.academico.dto.CriarCursoInput;
import com.henrique.ifconecta.domain.academico.model.Curso;
import com.henrique.ifconecta.domain.academico.model.Disciplina;
import com.henrique.ifconecta.domain.academico.port.CursoRepository;
import com.henrique.ifconecta.domain.academico.port.DisciplinaRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CriarCursoComDisciplinasUseCase {

    private final CursoRepository cursoRepository;
    private final DisciplinaRepository disciplinaRepository;

    @Transactional
    public void execute(CriarCursoInput input) {
        Curso curso = new Curso(input.nome(), input.sigla(), input.modalidade());
        Curso cursoSalvo = cursoRepository.salvar(curso);

        if (input.disciplinas() != null) {
            for (CriarCursoInput.DisciplinaInput discInput : input.disciplinas()) {
                Disciplina disciplina = new Disciplina(
                        cursoSalvo.getId(),
                        discInput.nome(),
                        discInput.cargaHoraria());
                disciplinaRepository.salvar(disciplina);
            }
        }
    }
}
