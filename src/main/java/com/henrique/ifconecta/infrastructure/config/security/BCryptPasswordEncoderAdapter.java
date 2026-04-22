package com.henrique.ifconecta.infrastructure.config.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.henrique.ifconecta.domain.usuario.port.PasswordEncoderPort;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BCryptPasswordEncoderAdapter implements PasswordEncoderPort {
    
    private final PasswordEncoder springPasswordEncoder;
    
    @Override
    public String encode(String rawPassword) {
        return springPasswordEncoder.encode(rawPassword);
    }
    
}
