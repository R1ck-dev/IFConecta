package com.henrique.ifconecta.desktop.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.henrique.ifconecta.desktop.core.http.ApiClient;
import com.henrique.ifconecta.desktop.model.MeuPerfil;
import com.henrique.ifconecta.desktop.model.UsuarioPublico;

/** Endpoints de usuário — cadastro, perfil e listagens. */
public final class UsuarioService {

    private UsuarioService() {
    }

    /** POST /api/usuarios/alunos — cadastro de aluno. cursoId pode ser nulo. */
    public static void registrarAluno(String cursoId, String nome, String email,
            String password, String prontuario) {
        Map<String, Object> body = new HashMap<>();
        body.put("cursoId", cursoId);
        body.put("nome", nome);
        body.put("email", email);
        body.put("password", password);
        body.put("prontuario", prontuario);
        ApiClient.post("/usuarios/alunos", body);
    }

    /** POST /api/usuarios/esqueci-senha */
    public static void esqueciSenha(String email) {
        ApiClient.post("/usuarios/esqueci-senha", Map.of("email", email));
    }

    /** GET /api/usuarios/me */
    public static MeuPerfil meuPerfil() {
        return ApiClient.get("/usuarios/me", MeuPerfil.class);
    }

    /** PATCH /api/usuarios/me — atualiza o nome. */
    public static void atualizarNome(String nome) {
        ApiClient.patch("/usuarios/me", Map.of("nome", nome));
    }

    /** PATCH /api/usuarios/me/senha */
    public static void alterarSenha(String senhaAtual, String novaSenha) {
        ApiClient.patch("/usuarios/me/senha", Map.of("senhaAtual", senhaAtual, "novaSenha", novaSenha));
    }

    /** GET /api/usuarios/professores */
    public static List<UsuarioPublico> professores() {
        return ApiClient.get("/usuarios/professores", new TypeReference<List<UsuarioPublico>>() {
        });
    }
}
