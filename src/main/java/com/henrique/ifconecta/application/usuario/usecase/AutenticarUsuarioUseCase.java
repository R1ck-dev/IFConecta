package com.henrique.ifconecta.application.usuario.usecase;

import org.springframework.stereotype.Service;

import com.henrique.ifconecta.application.usuario.dto.LoginInput;
import com.henrique.ifconecta.domain.usuario.model.Usuario;
import com.henrique.ifconecta.domain.usuario.port.AuthenticationPort;
import com.henrique.ifconecta.domain.usuario.port.TokenServicePort;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AutenticarUsuarioUseCase {
    
    private final AuthenticationPort authenticationPort;
    private final TokenServicePort tokenServicePort;

    public String execute(LoginInput input) {
        Usuario usuarioAutenticado = authenticationPort.autenticar(input.email(), input.password());

        return tokenServicePort.gerarToken(usuarioAutenticado);
    }
}
