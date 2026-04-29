package com.henrique.ifconecta.application.clube.usecase;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.henrique.ifconecta.application.post.dto.PostResumoDTO;
import com.henrique.ifconecta.domain.clube.enums.StatusMembro;
import com.henrique.ifconecta.domain.clube.enums.TipoAcesso;
import com.henrique.ifconecta.domain.clube.model.Clube;
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

        Clube clube = clubeRepository.buscarPorId(clubeId)
                .orElseThrow(() -> new NegocioException("Clube não encontrado."));

        if (clube.getTipoAcesso() == TipoAcesso.PRIVADO) {
            boolean isMembroAprovado = clube.getMembros().stream()
                    .anyMatch(m -> m.getUsuarioId().equals(usuarioAutenticadoId)
                            && m.getStatus() == StatusMembro.APROVADO);

            if (!isMembroAprovado) {
                throw new NegocioException("Apenas membros aprovados podem visualizar o conteúdo deste clube privado.");
            }
        }

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
