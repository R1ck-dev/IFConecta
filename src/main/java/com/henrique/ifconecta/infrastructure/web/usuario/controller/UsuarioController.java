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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Gerenciamento de alunos e ativação de contas")
public class UsuarioController {

    private final RegistrarAlunoUseCase registrarAlunoUseCase;
    private final AtivarContaUseCase ativarContaUseCase;
    private final AtivarConvidadoUseCase ativarConvidadoUseCase;

    @Operation(summary = "Registrar Aluno", description = "Cria a conta de um novo aluno. Requer um e-mail acadêmico válido.")
    @ApiResponse(responseCode = "201", description = "Aluno registrado com sucesso. E-mail de ativação enviado.")
    @ApiResponse(responseCode = "400", description = "Dados inválidos, e-mail já registrado ou curso inexistente.")
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

    @Operation(summary = "Ativar Conta (Aluno)", description = "Ativa a conta de um aluno através do token enviado por e-mail.")
    @ApiResponse(responseCode = "200", description = "Conta ativada com sucesso")
    @ApiResponse(responseCode = "400", description = "Token inválido ou expirado")
    @GetMapping("/ativar")
    public ResponseEntity<Map<String, String>> ativarConta(@RequestParam String token) {
        ativarContaUseCase.execute(token);

        Map<String, String> response = new HashMap<>();
        response.put("mensagem", "Conta ativada com sucesso! Você já pode realizar o login.");

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Ativar Conta de Convidado", description = "Ativa a conta e define a senha para Professores ou Institucionais convidados pelo Admin.")
    @ApiResponse(responseCode = "200", description = "Conta ativada e senha definida com sucesso")
    @ApiResponse(responseCode = "400", description = "Token inválido ou expirado")
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
