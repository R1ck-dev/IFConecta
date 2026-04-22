package com.henrique.ifconecta.infrastructure.web.usuario.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.henrique.ifconecta.application.usuario.dto.RegistrarAlunoInput;
import com.henrique.ifconecta.application.usuario.usecase.RegistrarAlunoUseCase;
import com.henrique.ifconecta.infrastructure.web.usuario.dto.RegistrarAlunoRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
    
    private final RegistrarAlunoUseCase registrarAlunoUseCase;

    @PostMapping("/alunos")
    public ResponseEntity<Void> registrarAluno(@RequestBody @Valid RegistrarAlunoRequest request) {

        RegistrarAlunoInput input = new RegistrarAlunoInput(
            request.nome(),
            request.email(),
            request.password(),
            request.prontuario()
        );

        registrarAlunoUseCase.execute(input);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
