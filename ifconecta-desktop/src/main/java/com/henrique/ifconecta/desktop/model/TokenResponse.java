package com.henrique.ifconecta.desktop.model;

/** Resposta de POST /api/auth/login. */
public record TokenResponse(String token, String tipo) {
}
