package com.henrique.ifconecta.application.academico.usecase;

import org.springframework.stereotype.Service;

import com.henrique.ifconecta.application.academico.dto.CriarTurmaInput;
import com.henrique.ifconecta.domain.academico.model.Turma;
import com.henrique.ifconecta.domain.academico.port.TurmaRepository;
import com.henrique.ifconecta.domain.usuario.exception.NegocioException;
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
        Usuario professor = usuarioRepository.buscarPorId(input.professorId())
                .orElseThrow(() -> new NegocioException("Professor não encontrado."));

        if (!(professor instanceof Professor)) {
            throw new NegocioException("O usuário selecionado para responsável da turma deve ser um professor.");
        }

        Turma novaTurma = new Turma(
                input.disciplinaId(),
                input.professorId(),
                input.semestre(),
                input.codigoTurma()
        );

        turmaRepository.salvar(novaTurma);
    }
}
