package com.henrique.ifconecta.application.notificacao.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.henrique.ifconecta.domain.notificacao.port.NotificacaoRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MarcarTodasComoLidasUseCase {

    private final NotificacaoRepository notificacaoRepository;

    @Transactional
    public int execute(UUID usuarioId) {
        return notificacaoRepository.marcarTodasComoLidasPorUsuario(usuarioId);
    }
}
