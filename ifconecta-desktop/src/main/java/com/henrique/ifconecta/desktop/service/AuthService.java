package com.henrique.ifconecta.desktop.service;

import com.henrique.ifconecta.desktop.core.http.ApiClient;
import com.henrique.ifconecta.desktop.model.LoginRequest;
import com.henrique.ifconecta.desktop.model.MeuPerfil;
import com.henrique.ifconecta.desktop.model.TokenResponse;

/** Endpoints de autenticação e perfil — espelha services/auth.js do front web. */
public final class AuthService {

    private AuthService() {
    }

    /** POST /api/auth/login */
    public static TokenResponse login(String email, String password) {
        return ApiClient.post("/auth/login", new LoginRequest(email, password), TokenResponse.class);
    }

    /** GET /api/usuarios/me */
    public static MeuPerfil getMe() {
        return ApiClient.get("/usuarios/me", MeuPerfil.class);
    }
}
