package com.henrique.ifconecta.infrastructure.web.notificacao.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.henrique.ifconecta.application.notificacao.dto.EnviarComunicadoInput;
import com.henrique.ifconecta.application.notificacao.usecase.EnviarComunicadoUseCase;
import com.henrique.ifconecta.infrastructure.web.notificacao.dto.EnviarComunicadoRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/comunicados")
@RequiredArgsConstructor
public class ComunicadoController {

    private final EnviarComunicadoUseCase enviarComunicadoUseCase;

    @PostMapping
    public ResponseEntity<Void> enviar(@RequestBody @Valid EnviarComunicadoRequest request) {
        String remetenteIdStr = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        enviarComunicadoUseCase.execute(new EnviarComunicadoInput(
                UUID.fromString(remetenteIdStr),
                request.titulo(),
                request.mensagem(),
                request.tipoAlvo(),
                request.alvoId()
        ));
        
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}
