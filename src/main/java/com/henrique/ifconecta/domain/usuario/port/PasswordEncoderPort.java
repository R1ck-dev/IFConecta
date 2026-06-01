package com.henrique.ifconecta.domain.usuario.port;

public interface PasswordEncoderPort { // Adapter: BCryptPasswordEncoderAdapter (infrastructure/config/security)
    String encode(String rawPassword);
}
