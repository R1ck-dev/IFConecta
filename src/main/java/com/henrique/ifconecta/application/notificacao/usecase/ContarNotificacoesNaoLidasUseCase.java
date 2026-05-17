package com.henrique.ifconecta.application.notificacao.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.henrique.ifconecta.domain.notificacao.port.NotificacaoRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContarNotificacoesNaoLidasUseCase {

    private final NotificacaoRepository notificacaoRepository;

    @Transactional
    public long execute(UUID usuarioId) {
        return notificacaoRepository.contarNaoLidasPorUsuario(usuarioId);
    }
}
