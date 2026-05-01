package com.henrique.ifconecta.infrastructure.persistence.notificacao.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.henrique.ifconecta.domain.notificacao.enums.TipoAlvoComunicado;
import com.henrique.ifconecta.infrastructure.persistence.usuario.entity.UsuarioJpaEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "notificacoes")
@Getter
@Setter
public class NotificacaoJpaEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioJpaEntity usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "remetente_id", nullable = false)
    private UsuarioJpaEntity remetente;

    @Column(nullable = false, length = 100)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensagem;

    @Column(nullable = false)
    private boolean lida;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_alvo", nullable = false)
    private TipoAlvoComunicado tipoAlvo;

    @Column(name = "referencia_id")
    private UUID referenciaId;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;
}