package com.henrique.ifconecta.application.post.usecase;

import org.springframework.stereotype.Service;

import com.henrique.ifconecta.application.post.dto.CriarPostInput;
import com.henrique.ifconecta.domain.clube.port.ClubeRepository;
import com.henrique.ifconecta.domain.post.model.Post;
import com.henrique.ifconecta.domain.post.port.PostRepository;
import com.henrique.ifconecta.domain.usuario.exception.NegocioException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CriarPostUseCase {

    private final PostRepository postRepository;
    private final ClubeRepository clubeRepository;

    @Transactional
    public void execute(CriarPostInput input) {
        if (input.clubeId() != null) {
            clubeRepository.buscarPorId(input.clubeId())
                    .orElseThrow(() -> new NegocioException("Clube não encontrado."));

        }

        Post novoPost = new Post(
                input.autorId(),
                input.clubeId(),
                input.conteudo());

        postRepository.salvar(novoPost);
    }
}
