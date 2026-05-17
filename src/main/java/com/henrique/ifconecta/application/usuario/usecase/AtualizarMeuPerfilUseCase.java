package com.henrique.ifconecta.application.usuario.usecase;

import org.springframework.stereotype.Service;

import com.henrique.ifconecta.application.usuario.dto.AtualizarMeuPerfilInput;
import com.henrique.ifconecta.domain.usuario.exception.NegocioException;
import com.henrique.ifconecta.domain.usuario.model.Usuario;
import com.henrique.ifconecta.domain.usuario.port.UsuarioRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AtualizarMeuPerfilUseCase {

    private final UsuarioRepository usuarioRepository;

    @Transactional
    public void execute(AtualizarMeuPerfilInput input) {
        String nome = input.nome() != null ? input.nome().trim() : null;
        if (nome == null || nome.isBlank()) {
            throw new NegocioException("O nome é obrigatório.");
        }
        if (nome.split("\\s+").length < 2) {
            throw new NegocioException("Informe nome e sobrenome.");
        }

        Usuario usuario = usuarioRepository.buscarPorId(input.usuarioId())
                .orElseThrow(() -> new NegocioException("Usuário não encontrado."));

        usuario.atualizarNome(nome);
        usuarioRepository.salvar(usuario);
    }
}
