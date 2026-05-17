package com.henrique.ifconecta.infrastructure.web.notificacao.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.henrique.ifconecta.application.notificacao.dto.EnviarComunicadoInput;
import com.henrique.ifconecta.application.notificacao.dto.NotificacaoResumoDTO;
import com.henrique.ifconecta.application.notificacao.usecase.EnviarComunicadoUseCase;
import com.henrique.ifconecta.application.notificacao.usecase.ListarMinhasNotificacoesUseCase;
import com.henrique.ifconecta.application.notificacao.usecase.MarcarNotificacaoComoLidaUseCase;
import com.henrique.ifconecta.domain.shared.Pagina;
import com.henrique.ifconecta.infrastructure.config.security.CurrentUserId;
import com.henrique.ifconecta.infrastructure.web.notificacao.dto.EnviarComunicadoRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/comunicados")
@RequiredArgsConstructor
public class ComunicadoController {

    private final EnviarComunicadoUseCase enviarComunicadoUseCase;
    private final ListarMinhasNotificacoesUseCase listarMinhasNotificacoesUseCase;
    private final MarcarNotificacaoComoLidaUseCase marcarNotificacaoComoLidaUseCase;

    @PostMapping
    public ResponseEntity<Void> enviar(@RequestBody @Valid EnviarComunicadoRequest request,
            @CurrentUserId UUID remetenteId) {
        enviarComunicadoUseCase.execute(new EnviarComunicadoInput(
                remetenteId,
                request.titulo(),
                request.mensagem(),
                request.tipoAlvo(),
                request.alvoId()));

        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @GetMapping("/minhas")
    public ResponseEntity<Pagina<NotificacaoResumoDTO>> listarMinhasNotificacoes(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho,
            @CurrentUserId UUID usuarioId) {

        Pagina<NotificacaoResumoDTO> response = listarMinhasNotificacoesUseCase.execute(usuarioId, pagina, tamanho);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{notificacaoId}/lida")
    public ResponseEntity<Void> marcarComoLida(@PathVariable UUID notificacaoId, @CurrentUserId UUID usuarioId) {
        marcarNotificacaoComoLidaUseCase.execute(notificacaoId, usuarioId);
        return ResponseEntity.noContent().build();
    }
}
