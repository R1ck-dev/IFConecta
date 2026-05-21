package com.henrique.ifconecta.desktop.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.henrique.ifconecta.desktop.core.http.ApiClient;
import com.henrique.ifconecta.desktop.model.Pagina;
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
}
