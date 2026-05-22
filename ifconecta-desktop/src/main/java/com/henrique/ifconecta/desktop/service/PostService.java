package com.henrique.ifconecta.desktop.service;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.henrique.ifconecta.desktop.core.http.ApiClient;
import com.henrique.ifconecta.desktop.model.Pagina;
import com.henrique.ifconecta.desktop.model.PostDetalhe;
import com.henrique.ifconecta.desktop.model.PostResumo;

/** Endpoints de posts — espelha services/posts.js do front web. */
public final class PostService {

    private PostService() {
    }

    /** GET /api/posts?pagina=&tamanho= — timeline geral paginada. */
    public static Pagina<PostResumo> listarTimeline(int pagina, int tamanho) {
        return ApiClient.get(
                "/posts?pagina=" + pagina + "&tamanho=" + tamanho,
                new TypeReference<Pagina<PostResumo>>() {
                });
    }

    /** PUT /api/posts/{id}/upvote — alterna o upvote. */
    public static void upvote(String postId) {
        ApiClient.put("/posts/" + postId + "/upvote");
    }

    /** POST /api/posts — cria um post. clubeId pode ser nulo (post global). */
    public static void criar(String conteudo, String clubeId, boolean anonimo) {
        Map<String, Object> body = new HashMap<>();
        body.put("conteudo", conteudo);
        body.put("clubeId", clubeId);
        body.put("anonimo", anonimo);
        ApiClient.post("/posts", body);
    }

    /** GET /api/posts/{id} — detalhe do post com comentários. */
    public static PostDetalhe detalhe(String postId) {
        return ApiClient.get("/posts/" + postId, PostDetalhe.class);
    }

    /** POST /api/posts/{id}/comentarios */
    public static void comentar(String postId, String conteudo) {
        ApiClient.post("/posts/" + postId + "/comentarios", Map.of("conteudo", conteudo));
    }
}
