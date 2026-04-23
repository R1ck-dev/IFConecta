package com.henrique.ifconecta.infrastructure.config.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.henrique.ifconecta.domain.usuario.exception.NegocioException;
import com.henrique.ifconecta.domain.usuario.model.Usuario;
import com.henrique.ifconecta.domain.usuario.port.AuthenticationPort;
import com.henrique.ifconecta.domain.usuario.port.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SpringAuthenticationAdapter implements AuthenticationPort {
    
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public Usuario autenticar(String email, String rawPassword) {
        Usuario usuario = usuarioRepository.buscarPorEmail(email)
                .orElseThrow(() -> new NegocioException("Credenciais inválidas."));

        if (!passwordEncoder.matches(rawPassword, usuario.getSenhaHash())) {
            throw new NegocioException("Credenciais inválidas.");
        }

        return usuario;
    }

}
