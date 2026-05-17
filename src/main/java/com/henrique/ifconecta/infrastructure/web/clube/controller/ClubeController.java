package com.henrique.ifconecta.infrastructure.web.clube.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.henrique.ifconecta.application.clube.dto.ClubeResumoDTO;
import com.henrique.ifconecta.application.clube.dto.CriarClubeInput;
import com.henrique.ifconecta.application.clube.usecase.AvaliarMembroUseCase;
import com.henrique.ifconecta.application.clube.usecase.CriarClubeUseCase;
import com.henrique.ifconecta.application.clube.usecase.ListarClubesUseCase;
import com.henrique.ifconecta.application.clube.usecase.ListarTimelineDoClubeUseCase;
import com.henrique.ifconecta.application.clube.usecase.SolicitarEntradaClubeUseCase;
import com.henrique.ifconecta.application.post.dto.PostResumoDTO;
import com.henrique.ifconecta.domain.shared.Pagina;
import com.henrique.ifconecta.infrastructure.config.security.CurrentUserId;
import com.henrique.ifconecta.infrastructure.web.clube.dto.AvaliarMembroRequest;
import com.henrique.ifconecta.infrastructure.web.clube.dto.CriarClubeRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/clubes")
@RequiredArgsConstructor
public class ClubeController {

    private final AvaliarMembroUseCase avaliarMembroUseCase;
    private final SolicitarEntradaClubeUseCase solicitarEntradaClubeUseCase;
    private final CriarClubeUseCase criarClubeUseCase;
    private final ListarClubesUseCase listarClubesUseCase;
    private final ListarTimelineDoClubeUseCase listarTimelineDoClubeUseCase;

    @PostMapping
    public ResponseEntity<Void> criarClube(@RequestBody @Valid CriarClubeRequest request,
            @CurrentUserId UUID criadorId) {
        CriarClubeInput input = new CriarClubeInput(
                request.nome(),
                request.descricao(),
                request.tipoAcesso(),
                criadorId);

        criarClubeUseCase.execute(input);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/{clubeId}/membros")
    public ResponseEntity<Void> solicitarEntrada(@PathVariable UUID clubeId, @CurrentUserId UUID usuarioId) {
        solicitarEntradaClubeUseCase.execute(clubeId, usuarioId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{clubeId}/membros/{usuarioAlvoId}/avaliacao")
    public ResponseEntity<Void> avaliarMembro(@PathVariable UUID clubeId, @PathVariable UUID usuarioAlvoId,
            @RequestBody @Valid AvaliarMembroRequest request, @CurrentUserId UUID liderId) {
        avaliarMembroUseCase.execute(clubeId, liderId, usuarioAlvoId, request.aprovado());
        return ResponseEntity.noContent().build();
    }

    @GetMapping()
    public ResponseEntity<Pagina<ClubeResumoDTO>> listarClubes(@RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho) {
        Pagina<ClubeResumoDTO> response = listarClubesUseCase.execute(pagina, tamanho);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{clubeId}/posts")
    public ResponseEntity<Pagina<PostResumoDTO>> listarTimeline(
            @PathVariable UUID clubeId,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho,
            @CurrentUserId UUID usuarioId) {

        Pagina<PostResumoDTO> response = listarTimelineDoClubeUseCase.execute(clubeId, usuarioId, pagina, tamanho);

        return ResponseEntity.ok(response);
    }
}
