package com.henrique.ifconecta.application.usuario.usecase;

import org.springframework.stereotype.Service;

import com.henrique.ifconecta.application.usuario.dto.AlterarMinhaSenhaInput;
import com.henrique.ifconecta.domain.usuario.exception.NegocioException;
import com.henrique.ifconecta.domain.usuario.model.Usuario;
import com.henrique.ifconecta.domain.usuario.port.PasswordEncoderPort;
import com.henrique.ifconecta.domain.usuario.port.UsuarioRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlterarMinhaSenhaUseCase {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoderPort passwordEncoder;

    @Transactional
    public void execute(AlterarMinhaSenhaInput input) {
        if (input.senhaAtual() == null || input.senhaAtual().isBlank()) {
            throw new NegocioException("A senha atual é obrigatória.");
        }
        if (input.novaSenha() == null || input.novaSenha().length() < 8) {
            throw new NegocioException("A nova senha deve ter no mínimo 8 caracteres.");
        }
        if (input.novaSenha().equals(input.senhaAtual())) {
            throw new NegocioException("A nova senha deve ser diferente da atual.");
        }

        Usuario usuario = usuarioRepository.buscarPorId(input.usuarioId())
                .orElseThrow(() -> new NegocioException("Usuário não encontrado."));

        if (!passwordEncoder.matches(input.senhaAtual(), usuario.getSenhaHash())) {
            throw new NegocioException("Senha atual incorreta.");
        }

        usuario.definirSenha(passwordEncoder.encode(input.novaSenha()));
        usuarioRepository.salvar(usuario);
    }
}
