package com.henrique.ifconecta.domain.usuario.port;

import com.henrique.ifconecta.domain.usuario.model.Usuario;

public interface AuthenticationPort { // Adapter: SpringAuthenticationAdapter (infrastructure/config/security)
    Usuario autenticar(String email, String rawPassword);   
}
