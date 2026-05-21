package com.henrique.ifconecta.desktop.model;

/** Corpo de POST /api/auth/login. */
public record LoginRequest(String email, String password) {
}
