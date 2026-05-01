package com.henrique.ifconecta.infrastructure.web.usuario.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.henrique.ifconecta.application.usuario.dto.AtivarConvidadoInput;
import com.henrique.ifconecta.application.usuario.dto.RegistrarAlunoInput;
import com.henrique.ifconecta.application.usuario.usecase.AtivarContaUseCase;
import com.henrique.ifconecta.application.usuario.usecase.AtivarConvidadoUseCase;
import com.henrique.ifconecta.application.usuario.usecase.RegistrarAlunoUseCase;
import com.henrique.ifconecta.infrastructure.web.usuario.dto.DefinirSenhaRequest;
import com.henrique.ifconecta.infrastructure.web.usuario.dto.RegistrarAlunoRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final RegistrarAlunoUseCase registrarAlunoUseCase;
    private final AtivarContaUseCase ativarContaUseCase;
    private final AtivarConvidadoUseCase ativarConvidadoUseCase;

    @PostMapping("/alunos")
    public ResponseEntity<Void> registrarAluno(@RequestBody @Valid RegistrarAlunoRequest request) {

        RegistrarAlunoInput input = new RegistrarAlunoInput(
                request.cursoId(),
                request.nome(),
                request.email(),
                request.password(),
                request.prontuario());

        registrarAlunoUseCase.execute(input);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/ativar")
    public ResponseEntity<Map<String, String>> ativarConta(@RequestParam String token) {
        ativarContaUseCase.execute(token);

        Map<String, String> response = new HashMap<>();
        response.put("mensagem", "Conta ativada com sucesso! Você já pode realizar o login.");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/ativar-convidado")
    public ResponseEntity<Map<String, String>> ativarConvidado(@RequestBody @Valid DefinirSenhaRequest request) {
        ativarConvidadoUseCase.execute(new AtivarConvidadoInput(request.token(), request.novaSenha()));

        Map<String, String> response = new HashMap<>();
        response.put("mensagem", "Conta ativada e senha definida com sucesso!");
        return ResponseEntity.ok(response);
    }

    // @GetMapping("/me")
    // public ResponseEntity<String> testarAutenticacao() {
    // return ResponseEntity.ok("Estou logado!");
    // }
}
