package com.henrique.ifconecta.application.post.usecase;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.henrique.ifconecta.application.post.dto.PostResumoDTO;
import com.henrique.ifconecta.domain.post.model.Post;
import com.henrique.ifconecta.domain.post.port.PostRepository;
import com.henrique.ifconecta.domain.shared.Pagina;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ListarTimelineGeralUseCase {

    private final PostRepository postRepository;

    @Transactional
    public Pagina<PostResumoDTO> execute(int pagina, int tamanho) {
        Pagina<Post> paginaDePosts = postRepository.listarTimelineGeral(pagina, tamanho);

        List<PostResumoDTO> resumos = paginaDePosts.itens().stream()
                .map(post -> {
                    String nomeExibicao = post.isAnonimo() ? "Estudante Anônimo" : post.getAutorNome();

                    return new PostResumoDTO(
                            post.getId(),
                            nomeExibicao,
                            post.getConteudo(),
                            post.getQtdUpVotes(),
                            post.getComentarios().size(),
                            post.getDataCriacao());
                })
                .collect(Collectors.toList());

        return new Pagina<>(resumos, paginaDePosts.paginaAtual(),
                paginaDePosts.totalPaginas(), paginaDePosts.totalItens());
    }
}
