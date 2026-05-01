package com.henrique.ifconecta.application.usuario.usecase;

import org.springframework.stereotype.Service;

import com.henrique.ifconecta.application.usuario.dto.AtivarConvidadoInput;
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
public class AtivarConvidadoUseCase {

    private final TokenVerificacaoRepository tokenVerificacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoderPort passwordEncoderPort;

    @Transactional
    public void execute(AtivarConvidadoInput input) {
        TokenVerificacao tokenVerificacao = tokenVerificacaoRepository.buscarPorToken(input.token())
                .orElseThrow(() -> new NegocioException("Token de verificação inválido ou não encontrado."));

        tokenVerificacao.validar();

        Usuario usuario = tokenVerificacao.getUsuario();

        // Define a nova senha escolhida pelo professor/institucional
        String novoHash = passwordEncoderPort.encode(input.novaSenha());
        usuario.definirSenha(novoHash);

        // Ativa a conta
        usuario.ativarConta();
        tokenVerificacao.marcarComoUtilziado();

        usuarioRepository.salvar(usuario);
        tokenVerificacaoRepository.salvar(tokenVerificacao);
    }
}
