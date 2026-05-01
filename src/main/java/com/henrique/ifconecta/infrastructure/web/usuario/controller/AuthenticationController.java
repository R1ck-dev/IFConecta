package com.henrique.ifconecta.infrastructure.web.usuario.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.henrique.ifconecta.application.usuario.dto.LoginInput;
import com.henrique.ifconecta.application.usuario.usecase.AutenticarUsuarioUseCase;
import com.henrique.ifconecta.infrastructure.web.usuario.dto.LoginRequest;
import com.henrique.ifconecta.infrastructure.web.usuario.dto.TokenResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints para login e geração de tokens JWT")
public class AuthenticationController {

    private final AutenticarUsuarioUseCase autenticarUsuarioUseCase;

    @Operation(summary = "Realizar Login", description = "Autentica um usuário e retorna o token JWT.")
    @ApiResponse(responseCode = "200", description = "Login realizado com sucesso")
    @ApiResponse(responseCode = "400", description = "Credenciais inválidas ou conta pendente")
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest request) {
        LoginInput input = new LoginInput(
            request.email(),
            request.password()
        );

        String tokenJwt = autenticarUsuarioUseCase.execute(input);

        return ResponseEntity.ok(new TokenResponse(tokenJwt));
    }
}
