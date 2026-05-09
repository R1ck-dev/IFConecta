package com.henrique.ifconecta.infrastructure.web.post.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.henrique.ifconecta.application.post.dto.AdicionarComentarioInput;
import com.henrique.ifconecta.application.post.dto.CriarPostInput;
import com.henrique.ifconecta.application.post.usecase.AdicionarComentarioUseCase;
import com.henrique.ifconecta.application.post.usecase.CriarPostUseCase;
import com.henrique.ifconecta.infrastructure.web.post.dto.AdicionarComentarioRequest;
import com.henrique.ifconecta.infrastructure.web.post.dto.CriarPostRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Tag(name = "Posts", description = "Criação de posts, comentários e curtidas (upvotes)")
public class PostController {

    private final CriarPostUseCase criarPostUseCase;
    private final AdicionarComentarioUseCase adicionarComentarioUseCase;

    @Operation(summary = "Criar Post", description = "Publica um novo post, podendo ser na timeline geral ou vinculado a um clube. Suporta postagens anônimas.")
    @ApiResponse(responseCode = "201", description = "Post criado com sucesso")
    @PostMapping
    public ResponseEntity<Void> criarPost(@RequestBody @Valid CriarPostRequest request) {
        String userIdStr = extraiId();
        UUID autorId = UUID.fromString(userIdStr);

        CriarPostInput input = new CriarPostInput(
                autorId,
                request.clubeId(),
                request.conteudo(),
                request.anonimo());

        criarPostUseCase.execute(input);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Adicionar Comentário", description = "Comenta em um post existente.")
    @ApiResponse(responseCode = "201", description = "Comentário adicionado")
    @PostMapping("/{postId}/comentarios")
    public ResponseEntity<Void> adicionarComentario(@PathVariable UUID postId,
            @RequestBody @Valid AdicionarComentarioRequest request) {
        String userIdStr = extraiId();
        UUID autorId = UUID.fromString(userIdStr);

        AdicionarComentarioInput input = new AdicionarComentarioInput(
                postId,
                autorId,
                request.conteudo());

        adicionarComentarioUseCase.execute(input);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    private String extraiId() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
