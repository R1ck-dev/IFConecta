package com.henrique.ifconecta.domain.post.port;

import java.util.Optional;
import java.util.UUID;

import com.henrique.ifconecta.domain.post.model.Post;
import com.henrique.ifconecta.domain.shared.Pagina;

public interface PostRepository {
    Post salvar(Post post);
    Optional<Post> buscarPorId(UUID id);
    Pagina<Post> listarTimelineGeral(int pagina, int tamanho);
}
