package com.henrique.ifconecta.application.usuario.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.henrique.ifconecta.application.usuario.dto.UsuarioPublicoDTO;
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
public class BuscarUsuarioPublicoUseCase {

    private final UsuarioRepository usuarioRepository;

    @Transactional
    public UsuarioPublicoDTO execute(UUID usuarioId) {
        Usuario usuario = usuarioRepository.buscarPorId(usuarioId)
                .orElseThrow(() -> new NegocioException("Usuário não encontrado."));

        return new UsuarioPublicoDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getRole(),
                tipoDe(usuario),
                usuario.getCursoId());
    }

    private String tipoDe(Usuario usuario) {
        if (usuario instanceof Aluno) return "ALUNO";
        if (usuario instanceof Professor) return "PROFESSOR";
        if (usuario instanceof Institucional) return "INSTITUCIONAL";
        throw new IllegalStateException("Tipo de usuário desconhecido: " + usuario.getClass().getSimpleName());
    }
}
