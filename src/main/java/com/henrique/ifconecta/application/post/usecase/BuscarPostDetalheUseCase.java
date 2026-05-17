package com.henrique.ifconecta.application.post.usecase;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.henrique.ifconecta.application.post.dto.ComentarioDTO;
import com.henrique.ifconecta.application.post.dto.PostDetalheDTO;
import com.henrique.ifconecta.domain.post.model.Comentario;
import com.henrique.ifconecta.domain.post.model.Post;
import com.henrique.ifconecta.domain.post.port.PostRepository;
import com.henrique.ifconecta.domain.usuario.exception.NegocioException;
import com.henrique.ifconecta.domain.usuario.model.Usuario;
import com.henrique.ifconecta.domain.usuario.port.UsuarioRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BuscarPostDetalheUseCase {

    private final PostRepository postRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public PostDetalheDTO execute(UUID postId, UUID usuarioLogadoId) {
        Post post = postRepository.buscarPorId(postId)
                .orElseThrow(() -> new NegocioException("Post não encontrado."));

        Map<UUID, String> nomesPorAutor = carregarNomesDosAutoresDeComentarios(post.getComentarios());

        List<ComentarioDTO> comentariosDTO = post.getComentarios().stream()
                .map(c -> new ComentarioDTO(
                        c.getId(),
                        c.getAutorId(),
                        nomesPorAutor.getOrDefault(c.getAutorId(), "Usuário desconhecido"),
                        c.getConteudo(),
                        c.getDataCriacao()))
                .toList();

        boolean autorAnonimo = post.isAnonimo();

        return new PostDetalheDTO(
                post.getId(),
                autorAnonimo ? null : post.getAutorId(),
                autorAnonimo ? null : post.getAutorNome(),
                post.getClubeId(),
                post.getConteudo(),
                post.isAnonimo(),
                post.getQtdUpVotes(),
                post.getUpvotes().contains(usuarioLogadoId),
                post.getDataCriacao(),
                comentariosDTO);
    }

    private Map<UUID, String> carregarNomesDosAutoresDeComentarios(List<Comentario> comentarios) {
        Set<UUID> idsAutores = comentarios.stream()
                .map(Comentario::getAutorId)
                .collect(Collectors.toSet());

        if (idsAutores.isEmpty()) {
            return Map.of();
        }

        return usuarioRepository.buscarPorIds(idsAutores).stream()
                .collect(Collectors.toMap(Usuario::getId, Usuario::getNome, (a, b) -> a));
    }
}
