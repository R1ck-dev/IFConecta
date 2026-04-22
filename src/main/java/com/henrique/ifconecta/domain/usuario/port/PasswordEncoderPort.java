package com.henrique.ifconecta.domain.usuario.port;

public interface PasswordEncoderPort {
    String encode(String rawPassword);
}
