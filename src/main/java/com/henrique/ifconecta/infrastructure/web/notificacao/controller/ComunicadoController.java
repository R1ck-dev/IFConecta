package com.henrique.ifconecta.infrastructure.web.notificacao.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.henrique.ifconecta.application.notificacao.dto.EnviarComunicadoInput;
import com.henrique.ifconecta.application.notificacao.dto.NotificacaoResumoDTO;
import com.henrique.ifconecta.application.notificacao.usecase.EnviarComunicadoUseCase;
import com.henrique.ifconecta.application.notificacao.usecase.ListarMinhasNotificacoesUseCase;
import com.henrique.ifconecta.domain.shared.Pagina;
import com.henrique.ifconecta.infrastructure.web.notificacao.dto.EnviarComunicadoRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/comunicados")
@RequiredArgsConstructor
@Tag(name = "Comunicados", description = "Envio e listagem de notificações do sistema")
public class ComunicadoController {

    private final EnviarComunicadoUseCase enviarComunicadoUseCase;
    private final ListarMinhasNotificacoesUseCase listarMinhasNotificacoesUseCase;

    @Operation(summary = "Enviar Comunicado", description = "Gera notificações em massa baseadas em um alvo (GERAL, CURSO, TURMA ou CLUBE).")
    @ApiResponse(responseCode = "202", description = "Comunicados processados e enviados")
    @PostMapping
    public ResponseEntity<Void> enviar(@RequestBody @Valid EnviarComunicadoRequest request) {
        String remetenteIdStr = extraiId();

        enviarComunicadoUseCase.execute(new EnviarComunicadoInput(
                UUID.fromString(remetenteIdStr),
                request.titulo(),
                request.mensagem(),
                request.tipoAlvo(),
                request.alvoId()));

        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @Operation(summary = "Minhas Notificações", description = "Lista as notificações recebidas pelo usuário logado.")
    @GetMapping("/minhas")
    public ResponseEntity<Pagina<NotificacaoResumoDTO>> listarMinhasNotificacoes(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho) {

        String userIdStr = extraiId();
        UUID usuarioId = UUID.fromString(userIdStr);

        Pagina<NotificacaoResumoDTO> response = listarMinhasNotificacoesUseCase.execute(usuarioId, pagina, tamanho);

        return ResponseEntity.ok(response);
    }

    private String extraiId() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
