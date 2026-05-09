package com.henrique.ifconecta.domain.usuario.port;

import java.util.Optional;

import com.henrique.ifconecta.domain.usuario.model.Usuario;

public interface TokenServicePort {
    String gerarToken(Usuario usuario);
    String obterIdDoUsuario(String token);
    Optional<String> obterRoleDoUsuario(String token);
}
