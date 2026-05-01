package com.henrique.ifconecta.domain.notificacao.port;

import java.util.List;

import com.henrique.ifconecta.domain.notificacao.model.Notificacao;

public interface NotificacaoRepository {
    // Usamos List para forçar o adapter a usar saveAll (Batch Insert) para performance
    void salvarEmLote(List<Notificacao> notificacoes);
}
