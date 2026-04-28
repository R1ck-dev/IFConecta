package com.henrique.ifconecta.application.post.usecase;

import org.springframework.stereotype.Service;

import com.henrique.ifconecta.application.post.dto.CriarPostInput;
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
public class CriarPostUseCase {

    private final PostRepository postRepository;
    private final ClubeRepository clubeRepository;

    @Transactional
    public void execute(CriarPostInput input) {
        if (input.clubeId() != null) {
            Clube clube = clubeRepository.buscarPorId(input.clubeId())
                    .orElseThrow(() -> new NegocioException("Clube não encontrado."));

            boolean isMembroAprovado = clube.getMembros().stream()
                    .anyMatch(member -> member.getUsuarioId().equals(input.autorId())
                            && member.getStatus() == StatusMembro.APROVADO);

            if (!isMembroAprovado) {
                throw new NegocioException("Apenas membros aprovados podem publicar neste clube.");
            }
        }

        Post novoPost = new Post(
            input.autorId(),
            input.clubeId(),
            input.conteudo(),
            input.anonimo()
        );

        postRepository.salvar(novoPost);
    }
}
