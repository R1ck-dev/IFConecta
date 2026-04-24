package com.henrique.ifconecta.application.usuario.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.henrique.ifconecta.domain.usuario.exception.NegocioException;
import com.henrique.ifconecta.domain.usuario.model.TokenVerificacao;
import com.henrique.ifconecta.domain.usuario.model.Usuario;
import com.henrique.ifconecta.domain.usuario.port.TokenVerificacaoRepository;
import com.henrique.ifconecta.domain.usuario.port.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AtivarContaUseCase {
    
    private final TokenVerificacaoRepository tokenVerificacaoRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public void execute(String token) {
        TokenVerificacao tokenVerificacao = tokenVerificacaoRepository.buscarPorToken(token)
                .orElseThrow(() -> new NegocioException("Token de verificação inválido ou não encontrado."));

        tokenVerificacao.validar();

        Usuario usuario = tokenVerificacao.getUsuario();
        usuario.ativarConta();;

        tokenVerificacao.marcarComoUtilziado();

        usuarioRepository.salvar(usuario);
        tokenVerificacaoRepository.salvar(tokenVerificacao);
    }
}
