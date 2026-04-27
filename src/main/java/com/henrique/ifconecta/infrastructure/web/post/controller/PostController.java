package com.henrique.ifconecta.infrastructure.web.post.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.henrique.ifconecta.application.post.dto.AdicionarComentarioInput;
import com.henrique.ifconecta.application.post.dto.CriarPostInput;
import com.henrique.ifconecta.application.post.usecase.AdicionarComentarioUseCase;
import com.henrique.ifconecta.application.post.usecase.CriarPostUseCase;
import com.henrique.ifconecta.application.post.usecase.DarUpvoteUseCase;
import com.henrique.ifconecta.infrastructure.web.post.dto.AdicionarComentarioRequest;
import com.henrique.ifconecta.infrastructure.web.post.dto.CriarPostRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final CriarPostUseCase criarPostUseCase;
    private final AdicionarComentarioUseCase adicionarComentarioUseCase;
    private final DarUpvoteUseCase darUpvoteUseCase;

    @PostMapping
    public ResponseEntity<Void> criarPost(@RequestBody @Valid CriarPostRequest request) {
        String userIdStr = extraiId();
        UUID autorId = UUID.fromString(userIdStr);

        CriarPostInput input = new CriarPostInput(
            autorId,
            request.clubeId(),
            request.conteudo()
        );

        criarPostUseCase.execute(input);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/{postId}/comentarios")
    public ResponseEntity<Void> adicionarComentario(@PathVariable UUID postId, @RequestBody @Valid AdicionarComentarioRequest request) {
        String userIdStr = extraiId();
        UUID autorId = UUID.fromString(userIdStr);

        AdicionarComentarioInput input = new AdicionarComentarioInput(
            postId,
            autorId,
            request.conteudo()
        );

        adicionarComentarioUseCase.execute(input);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{postId}/upvote")
    public ResponseEntity<Void> darUpVote(@PathVariable UUID postId) {
        String userIdStr = extraiId();
        UUID autorId = UUID.fromString(userIdStr);

        darUpvoteUseCase.execute(postId, autorId);

        return ResponseEntity.noContent().build();
    }
    

    private String extraiId() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
