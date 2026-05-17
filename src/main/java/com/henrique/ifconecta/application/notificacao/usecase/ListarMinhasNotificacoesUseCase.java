package com.henrique.ifconecta.application.notificacao.usecase;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.henrique.ifconecta.application.notificacao.dto.NotificacaoResumoDTO;
import com.henrique.ifconecta.domain.notificacao.model.Notificacao;
import com.henrique.ifconecta.domain.notificacao.port.NotificacaoRepository;
import com.henrique.ifconecta.domain.shared.Pagina;
import com.henrique.ifconecta.domain.usuario.model.Usuario;
import com.henrique.ifconecta.domain.usuario.port.UsuarioRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ListarMinhasNotificacoesUseCase {

    private final NotificacaoRepository notificacaoRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public Pagina<NotificacaoResumoDTO> execute(UUID usuarioId, int pagina, int tamanho) {

        Pagina<Notificacao> paginaNotificacoes = notificacaoRepository.listarPorUsuario(usuarioId, pagina, tamanho);

        List<UUID> remetentesIds = paginaNotificacoes.itens().stream()
                .map(Notificacao::getRemetenteId)
                .distinct()
                .collect(Collectors.toList());

        Map<UUID, Usuario> remetentes = remetentesIds.isEmpty()
                ? Map.of()
                : usuarioRepository.buscarPorIds(remetentesIds).stream()
                        .collect(Collectors.toMap(Usuario::getId, Function.identity()));

        List<NotificacaoResumoDTO> resumos = paginaNotificacoes.itens().stream()
                .map(notificacao -> {
                    Usuario remetente = remetentes.get(notificacao.getRemetenteId());
                    return new NotificacaoResumoDTO(
                            notificacao.getId(),
                            notificacao.getTitulo(),
                            notificacao.getMensagem(),
                            notificacao.isLida(),
                            notificacao.getTipoAlvo(),
                            notificacao.getReferenciaId(),
                            notificacao.getDataCriacao(),
                            notificacao.getRemetenteId(),
                            remetente != null ? remetente.getNome() : "Sistema");
                })
                .collect(Collectors.toList());

        return new Pagina<>(resumos, paginaNotificacoes.paginaAtual(),
                paginaNotificacoes.totalPaginas(), paginaNotificacoes.totalItens());
    }
}
