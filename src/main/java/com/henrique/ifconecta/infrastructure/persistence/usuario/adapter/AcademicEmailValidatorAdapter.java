package com.henrique.ifconecta.infrastructure.persistence.usuario.adapter;

import java.util.List;

import org.springframework.stereotype.Component;

import com.henrique.ifconecta.domain.usuario.port.EmailValidatorPort;

@Component
public class AcademicEmailValidatorAdapter implements EmailValidatorPort{

    // Caso existem variações para professores ou institucionais, adicionamos aqui.
    private final List<String> dominiosPermitidos = List.of(
        "@aluno.ifsp.edu.br",
        "@ifsp.edu.br"
    ); 

    @Override
    public boolean isValidAcademicEmail(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }

        String emailFormatado = email.trim().toLowerCase();

        return dominiosPermitidos.stream().anyMatch(emailFormatado::endsWith);
    }
    
}
