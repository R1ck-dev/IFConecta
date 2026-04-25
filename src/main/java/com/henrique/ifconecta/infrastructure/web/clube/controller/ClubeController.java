package com.henrique.ifconecta.infrastructure.web.clube.controller;

import com.henrique.ifconecta.application.clube.usecase.SolicitarEntradaClubeUseCase;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.henrique.ifconecta.application.clube.dto.CriarClubeInput;
import com.henrique.ifconecta.application.clube.usecase.CriarClubeUseCase;
import com.henrique.ifconecta.infrastructure.web.clube.dto.CriarClubeRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/clubes")
@RequiredArgsConstructor
public class ClubeController {
    
    private final SolicitarEntradaClubeUseCase solicitarEntradaClubeUseCase;
    private final CriarClubeUseCase criarClubeUseCase;

    @PostMapping
    public ResponseEntity<Void> criarClube(@RequestBody @Valid CriarClubeRequest request) {
        // Extraímos o ID do utilizador que o JwtAuthenticationFilter colocou no contexto
        String userIdStr = extraiId();
        UUID criadorId = UUID.fromString(userIdStr);

        CriarClubeInput input = new CriarClubeInput(
            request.nome(),
            request.descricao(),
            request.tipoAcesso(),
            criadorId
        );

        criarClubeUseCase.execute(input);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/{clubeId}/membros")
    public ResponseEntity<Void> solicitarEntrada(@PathVariable UUID cluebId) {
        String userIdStr = extraiId();
        UUID usuarioId = UUID.fromString(userIdStr);
        
        solicitarEntradaClubeUseCase.execute(cluebId, usuarioId);

        return ResponseEntity.ok().build();
    }

    private String extraiId() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
    
}
