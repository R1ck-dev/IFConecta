package com.henrique.ifconecta.application.clube.usecase;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.henrique.ifconecta.application.post.dto.PostResumoDTO;
import com.henrique.ifconecta.domain.clube.port.ClubeRepository;
import com.henrique.ifconecta.domain.post.port.PostRepository;
import com.henrique.ifconecta.domain.shared.Pagina;
import com.henrique.ifconecta.domain.usuario.exception.NegocioException;
// import com.henrique.ifconecta.domain.usuario.port.UsuarioRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ListarTimelineDoClubeUseCase {

    private final PostRepository postRepository;
    private final ClubeRepository clubeRepository;
    // private final UsuarioRepository usuarioRepository;

    @Transactional
    public Pagina<PostResumoDTO> execute(UUID clubeId, UUID usuarioAutenticadoId, int pagina, int tamanho) {

        clubeRepository.buscarPorId(clubeId)
                .orElseThrow(() -> new NegocioException("Clube não encontrado."));

        var paginaDePosts = postRepository.listarTimelineDoClube(clubeId, pagina, tamanho);

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
