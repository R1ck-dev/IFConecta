package com.henrique.ifconecta.application.usuario.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.henrique.ifconecta.application.usuario.dto.MeuPerfilDTO;
import com.henrique.ifconecta.domain.usuario.exception.NegocioException;
import com.henrique.ifconecta.domain.usuario.model.Aluno;
import com.henrique.ifconecta.domain.usuario.model.Institucional;
import com.henrique.ifconecta.domain.usuario.model.Professor;
import com.henrique.ifconecta.domain.usuario.model.Usuario;
import com.henrique.ifconecta.domain.usuario.port.UsuarioRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BuscarMeuPerfilUseCase {

    private final UsuarioRepository usuarioRepository;

    @Transactional
    public MeuPerfilDTO execute(UUID usuarioId) {
        Usuario usuario = usuarioRepository.buscarPorId(usuarioId)
                .orElseThrow(() -> new NegocioException("Usuário não encontrado."));

        return toDTO(usuario);
    }

    private MeuPerfilDTO toDTO(Usuario usuario) {
        if (usuario instanceof Aluno aluno) {
            return new MeuPerfilDTO(
                    aluno.getId(),
                    aluno.getNome(),
                    aluno.getEmailAcad(),
                    aluno.getRole(),
                    aluno.getStatus(),
                    aluno.getCursoId(),
                    aluno.getDataCriacao(),
                    "ALUNO",
                    aluno.getProntuario(),
                    null,
                    null,
                    null);
        }

        if (usuario instanceof Professor professor) {
            return new MeuPerfilDTO(
                    professor.getId(),
                    professor.getNome(),
                    professor.getEmailAcad(),
                    professor.getRole(),
                    professor.getStatus(),
                    professor.getCursoId(),
                    professor.getDataCriacao(),
                    "PROFESSOR",
                    null,
                    professor.getSiape(),
                    null,
                    null);
        }

        if (usuario instanceof Institucional institucional) {
            return new MeuPerfilDTO(
                    institucional.getId(),
                    institucional.getNome(),
                    institucional.getEmailAcad(),
                    institucional.getRole(),
                    institucional.getStatus(),
                    institucional.getCursoId(),
                    institucional.getDataCriacao(),
                    "INSTITUCIONAL",
                    null,
                    null,
                    institucional.getSetor(),
                    institucional.getCargo());
        }

        throw new IllegalStateException("Tipo de usuário desconhecido: " + usuario.getClass().getSimpleName());
    }
}
