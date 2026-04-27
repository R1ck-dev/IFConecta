package com.henrique.ifconecta.application.post.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.henrique.ifconecta.domain.post.model.Post;
import com.henrique.ifconecta.domain.post.port.PostRepository;
import com.henrique.ifconecta.domain.usuario.exception.NegocioException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DarUpvoteUseCase {
    
    private final PostRepository postRepository;

    @Transactional
    public void execute(UUID postId, UUID usuarioId) {
        Post post = postRepository.buscarPorId(postId)
                .orElseThrow(() -> new NegocioException("Post não encontrado."));

        post.darUpVote(usuarioId);

        postRepository.salvar(post);
    }
}
