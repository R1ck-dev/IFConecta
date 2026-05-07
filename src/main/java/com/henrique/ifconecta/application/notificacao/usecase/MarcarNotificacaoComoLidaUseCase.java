package com.henrique.ifconecta.application.notificacao.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.henrique.ifconecta.domain.notificacao.model.Notificacao;
import com.henrique.ifconecta.domain.notificacao.port.NotificacaoRepository;
import com.henrique.ifconecta.domain.usuario.exception.NegocioException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MarcarNotificacaoComoLidaUseCase {

    private final NotificacaoRepository notificacaoRepository;

    @Transactional
    public void execute(UUID notificacaoId, UUID usuarioId) {
        
        Notificacao notificacao = notificacaoRepository.buscarPorId(notificacaoId)
                .orElseThrow(() -> new NegocioException("Notificação não encontrada."));

        if (!notificacao.getUsuarioId().equals(usuarioId)) {
            throw new NegocioException("Você não tem permissão para alterar esta notificação.");
        }

        notificacao.marcarComoLida();

        notificacaoRepository.salvar(notificacao);
    }
}
