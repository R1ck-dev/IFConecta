package com.henrique.ifconecta.infrastructure.web.clube.controller;

import com.henrique.ifconecta.application.post.dto.PostResumoDTO;
import com.henrique.ifconecta.domain.shared.Pagina;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.henrique.ifconecta.application.clube.dto.ClubeResumoDTO;
import com.henrique.ifconecta.application.clube.dto.CriarClubeInput;
import com.henrique.ifconecta.application.clube.usecase.CriarClubeUseCase;
import com.henrique.ifconecta.application.clube.usecase.ListarClubesUseCase;
import com.henrique.ifconecta.application.clube.usecase.ListarTimelineDoClubeUseCase;
import com.henrique.ifconecta.infrastructure.web.clube.dto.CriarClubeRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/clubes")
@RequiredArgsConstructor
@Tag(name = "Clubes", description = "Criação de clubes e visualização de timelines")
public class ClubeController {

    private final CriarClubeUseCase criarClubeUseCase;
    private final ListarClubesUseCase listarClubesUseCase;
    private final ListarTimelineDoClubeUseCase listarTimelineDoClubeUseCase;

    @Operation(summary = "Criar Clube", description = "Cria um novo clube. O usuário criador torna-se o líder automaticamente.")
    @ApiResponse(responseCode = "201", description = "Clube criado com sucesso")
    @PostMapping
    public ResponseEntity<Void> criarClube(@RequestBody @Valid CriarClubeRequest request) {
        UUID criadorId = UUID.fromString(extraiId());

        CriarClubeInput input = new CriarClubeInput(request.nome(), request.descricao(), criadorId);
        criarClubeUseCase.execute(input);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Listar Clubes", description = "Retorna uma lista paginada de todos os clubes ativos.")
    @GetMapping()
    public ResponseEntity<Pagina<ClubeResumoDTO>> listarClubes(@RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho) {
        return ResponseEntity.ok(listarClubesUseCase.execute(pagina, tamanho));
    }

    @Operation(summary = "Timeline do Clube", description = "Retorna publicamente os posts feitos dentro de um clube específico.")
    @ApiResponse(responseCode = "200", description = "Timeline recuperada com sucesso")
    @GetMapping("/{clubeId}/posts")
    public ResponseEntity<Pagina<PostResumoDTO>> listarTimeline(@PathVariable UUID clubeId,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho) {
        return ResponseEntity.ok(listarTimelineDoClubeUseCase.execute(clubeId, pagina, tamanho));
    }

    private String extraiId() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
