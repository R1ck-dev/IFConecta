package com.henrique.ifconecta.application.usuario.usecase;

import java.util.List;

import org.springframework.stereotype.Service;

import com.henrique.ifconecta.application.usuario.dto.UsuarioPublicoDTO;
import com.henrique.ifconecta.domain.usuario.model.Professor;
import com.henrique.ifconecta.domain.usuario.port.UsuarioRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ListarProfessoresAtivosUseCase {

    private final UsuarioRepository usuarioRepository;

    @Transactional
    public List<UsuarioPublicoDTO> execute() {
        return usuarioRepository.listarProfessoresAtivos().stream()
                .filter(Professor.class::isInstance)
                .map(u -> new UsuarioPublicoDTO(u.getId(), u.getNome(), u.getRole(), "PROFESSOR", u.getCursoId()))
                .toList();
    }
}
