package com.henrique.ifconecta.infrastructure.web.usuario.dto;

public record TokenResponse(
    String token,
    String tipo
) {
    public TokenResponse(String token) {
        this(token, "Bearer");
    }
} 
