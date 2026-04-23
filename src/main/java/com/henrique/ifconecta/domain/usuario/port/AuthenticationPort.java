package com.henrique.ifconecta.domain.usuario.port;

import com.henrique.ifconecta.domain.usuario.model.Usuario;

public interface AuthenticationPort {
    Usuario autenticar(String email, String rawPassword);   
}
