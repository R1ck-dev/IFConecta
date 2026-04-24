package com.henrique.ifconecta.domain.usuario.port;

import java.util.Optional;

import com.henrique.ifconecta.domain.usuario.model.TokenVerificacao;

public interface TokenVerificacaoRepository {
    TokenVerificacao salvar(TokenVerificacao token);
    Optional<TokenVerificacao> buscarPorToken(String token);
}