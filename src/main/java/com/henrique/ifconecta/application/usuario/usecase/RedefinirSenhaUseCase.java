package com.henrique.ifconecta.application.usuario.usecase;

import org.springframework.stereotype.Service;

import com.henrique.ifconecta.application.usuario.dto.RedefinirSenhaInput;
import com.henrique.ifconecta.domain.usuario.enums.TipoToken;
import com.henrique.ifconecta.domain.usuario.exception.NegocioException;
import com.henrique.ifconecta.domain.usuario.model.TokenVerificacao;
import com.henrique.ifconecta.domain.usuario.model.Usuario;
import com.henrique.ifconecta.domain.usuario.port.PasswordEncoderPort;
import com.henrique.ifconecta.domain.usuario.port.TokenVerificacaoRepository;
import com.henrique.ifconecta.domain.usuario.port.UsuarioRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedefinirSenhaUseCase {

    private final TokenVerificacaoRepository tokenVerificacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoderPort passwordEncoder;

    @Transactional
    public void execute(RedefinirSenhaInput input) {
        if (input.novaSenha() == null || input.novaSenha().length() < 8) {
            throw new NegocioException("A nova senha deve ter no mínimo 8 caracteres.");
        }

        TokenVerificacao token = tokenVerificacaoRepository.buscarPorToken(input.token())
                .orElseThrow(() -> new NegocioException("Token inválido ou expirado."));

        if (token.getTipo() != TipoToken.REDEFINICAO_SENHA) {
            throw new NegocioException("Token inválido para redefinição de senha.");
        }

        token.validar();

        Usuario usuario = token.getUsuario();
        usuario.definirSenha(passwordEncoder.encode(input.novaSenha()));
        token.marcarComoUtilziado();

        usuarioRepository.salvar(usuario);
        tokenVerificacaoRepository.salvar(token);
    }
}
