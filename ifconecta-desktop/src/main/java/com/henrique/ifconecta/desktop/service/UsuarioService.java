package com.henrique.ifconecta.desktop.service;

import java.util.HashMap;
import java.util.Map;

import com.henrique.ifconecta.desktop.core.http.ApiClient;

/** Endpoints de usuário — cadastro de aluno. */
public final class UsuarioService {

    private UsuarioService() {
    }

    /** POST /api/usuarios/alunos — cadastro de aluno. */
    public static void registrarAluno(String nome, String email,
            String password, String prontuario) {
        Map<String, Object> body = new HashMap<>();
        body.put("nome", nome);
        body.put("email", email);
        body.put("password", password);
        body.put("prontuario", prontuario);
        ApiClient.post("/usuarios/alunos", body);
    }
}
