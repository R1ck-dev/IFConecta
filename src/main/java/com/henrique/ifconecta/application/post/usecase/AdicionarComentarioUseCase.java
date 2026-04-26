package com.henrique.ifconecta.application.post.usecase;

import org.springframework.stereotype.Service;

import com.henrique.ifconecta.application.post.dto.AdicionarComentarioInput;
import com.henrique.ifconecta.domain.clube.enums.StatusMembro;
import com.henrique.ifconecta.domain.clube.model.Clube;
import com.henrique.ifconecta.domain.clube.port.ClubeRepository;
import com.henrique.ifconecta.domain.post.model.Post;
import com.henrique.ifconecta.domain.post.port.PostRepository;
import com.henrique.ifconecta.domain.usuario.exception.NegocioException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdicionarComentarioUseCase {

    private final PostRepository postRepository;
    private final ClubeRepository clubeRepository;

    @Transactional
    public void execute(AdicionarComentarioInput input) {
        Post post = postRepository.buscarPorId(input.postId())
                .orElseThrow(() -> new NegocioException("Post não encontrado."));

        if (post.getClubeId() != null) {
            Clube clube = clubeRepository.buscarPorId(post.getClubeId())
                    .orElseThrow(() -> new NegocioException("Clube não encontrado"));

            boolean isMembroAprovado = clube.getMembros().stream()
                    .anyMatch(member -> member.getUsuarioId().equals(input.autorId())
                            && member.getStatus() == StatusMembro.APROVADO);

            if (!isMembroAprovado) {
                throw new NegocioException("Apenas membros podem comentar nos posts deste clube.");
            }
        }

        post.adicionarComentario(input.autorId(), input.conteudo());

        postRepository.salvar(post);
    }
}
