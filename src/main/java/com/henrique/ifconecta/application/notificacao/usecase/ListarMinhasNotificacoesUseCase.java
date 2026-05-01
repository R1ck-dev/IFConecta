package com.henrique.ifconecta.application.notificacao.usecase;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.henrique.ifconecta.application.notificacao.dto.NotificacaoResumoDTO;
import com.henrique.ifconecta.domain.notificacao.model.Notificacao;
import com.henrique.ifconecta.domain.notificacao.port.NotificacaoRepository;
import com.henrique.ifconecta.domain.shared.Pagina;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ListarMinhasNotificacoesUseCase {

    private final NotificacaoRepository notificacaoRepository;

    @Transactional
    public Pagina<NotificacaoResumoDTO> execute(UUID usuarioId, int pagina, int tamanho) {
        
        Pagina<Notificacao> paginaNotificacoes = notificacaoRepository.listarPorUsuario(usuarioId, pagina, tamanho);

        List<NotificacaoResumoDTO> resumos = paginaNotificacoes.itens().stream()
                .map(notificacao -> new NotificacaoResumoDTO(
                        notificacao.getId(),
                        notificacao.getTitulo(),
                        notificacao.getMensagem(),
                        notificacao.isLida(),
                        notificacao.getTipoAlvo(),
                        notificacao.getReferenciaId(),
                        notificacao.getDataCriacao()
                ))
                .collect(Collectors.toList());

        return new Pagina<>(resumos, paginaNotificacoes.paginaAtual(), 
                            paginaNotificacoes.totalPaginas(), paginaNotificacoes.totalItens());
    }
}
