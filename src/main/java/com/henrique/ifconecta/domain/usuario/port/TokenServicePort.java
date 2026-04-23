package com.henrique.ifconecta.domain.usuario.port;

import com.henrique.ifconecta.domain.usuario.model.Usuario;

public interface TokenServicePort {
    String gerarToken(Usuario usuario);
    String obterIdDoUsuario(String token);
    String obterRoleDoUsuario(String token);
}
