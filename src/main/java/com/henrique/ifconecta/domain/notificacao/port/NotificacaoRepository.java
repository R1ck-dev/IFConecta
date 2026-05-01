package com.henrique.ifconecta.domain.notificacao.port;

import java.util.List;
import java.util.UUID;

import com.henrique.ifconecta.domain.notificacao.model.Notificacao;
import com.henrique.ifconecta.domain.shared.Pagina;

public interface NotificacaoRepository {
    // Usamos List para forçar o adapter a usar saveAll (Batch Insert) para performance
    void salvarEmLote(List<Notificacao> notificacoes);
    Pagina<Notificacao> listarPorUsuario(UUID usuarioId, int pagina, int tamanho);
}
