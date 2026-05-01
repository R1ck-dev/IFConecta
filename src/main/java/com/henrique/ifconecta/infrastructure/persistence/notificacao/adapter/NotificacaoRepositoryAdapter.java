package com.henrique.ifconecta.infrastructure.persistence.notificacao.adapter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.henrique.ifconecta.domain.notificacao.model.Notificacao;
import com.henrique.ifconecta.domain.notificacao.port.NotificacaoRepository;
import com.henrique.ifconecta.infrastructure.persistence.notificacao.entity.NotificacaoJpaEntity;
import com.henrique.ifconecta.infrastructure.persistence.notificacao.mapper.NotificacaoMapper;
import com.henrique.ifconecta.infrastructure.persistence.notificacao.repository.SpringDataNotificacaoRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificacaoRepositoryAdapter implements NotificacaoRepository {
    private final SpringDataNotificacaoRepository repository;
    private final NotificacaoMapper mapper; 

    @Override
    public void salvarEmLote(List<Notificacao> notificacoes) {
        List<NotificacaoJpaEntity> entities = notificacoes.stream()
                .map(mapper::toEntity)
                .collect(Collectors.toList());
        repository.saveAll(entities); 
    }
}
