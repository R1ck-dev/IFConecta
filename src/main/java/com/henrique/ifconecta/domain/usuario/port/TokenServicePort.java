package com.henrique.ifconecta.domain.usuario.port;

import com.henrique.ifconecta.domain.usuario.model.Usuario;

public interface TokenServicePort { // Adapter: JwtTokenAdapter (infrastructure/config/security)
    String gerarToken(Usuario usuario);
    String obterIdDoUsuario(String token);
    String obterRoleDoUsuario(String token);
}
