package com.henrique.ifconecta.infrastructure.web.usuario.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.henrique.ifconecta.application.usuario.dto.AlterarMinhaSenhaInput;
import com.henrique.ifconecta.application.usuario.dto.AtivarConvidadoInput;
import com.henrique.ifconecta.application.usuario.dto.AtualizarMeuPerfilInput;
import com.henrique.ifconecta.application.usuario.dto.MeuPerfilDTO;
import com.henrique.ifconecta.application.usuario.dto.RegistrarAlunoInput;
import com.henrique.ifconecta.application.usuario.dto.UsuarioPublicoDTO;
import com.henrique.ifconecta.application.usuario.usecase.AlterarMinhaSenhaUseCase;
import com.henrique.ifconecta.application.usuario.usecase.AtivarContaUseCase;
import com.henrique.ifconecta.application.usuario.usecase.AtivarConvidadoUseCase;
import com.henrique.ifconecta.application.usuario.usecase.AtualizarMeuPerfilUseCase;
import com.henrique.ifconecta.application.usuario.usecase.BuscarMeuPerfilUseCase;
import com.henrique.ifconecta.application.usuario.usecase.BuscarUsuarioPublicoUseCase;
import com.henrique.ifconecta.application.usuario.usecase.RegistrarAlunoUseCase;
import com.henrique.ifconecta.infrastructure.config.security.CurrentUserId;
import com.henrique.ifconecta.infrastructure.web.usuario.dto.AlterarMinhaSenhaRequest;
import com.henrique.ifconecta.infrastructure.web.usuario.dto.AtualizarMeuPerfilRequest;
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
    private final BuscarMeuPerfilUseCase buscarMeuPerfilUseCase;
    private final BuscarUsuarioPublicoUseCase buscarUsuarioPublicoUseCase;
    private final AtualizarMeuPerfilUseCase atualizarMeuPerfilUseCase;
    private final AlterarMinhaSenhaUseCase alterarMinhaSenhaUseCase;

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

    @GetMapping("/me")
    public ResponseEntity<MeuPerfilDTO> meuPerfil(@CurrentUserId UUID usuarioId) {
        return ResponseEntity.ok(buscarMeuPerfilUseCase.execute(usuarioId));
    }

    @PatchMapping("/me")
    public ResponseEntity<Void> atualizarMeuPerfil(@CurrentUserId UUID usuarioId,
            @RequestBody @Valid AtualizarMeuPerfilRequest request) {
        atualizarMeuPerfilUseCase.execute(new AtualizarMeuPerfilInput(usuarioId, request.nome()));
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/me/senha")
    public ResponseEntity<Void> alterarMinhaSenha(@CurrentUserId UUID usuarioId,
            @RequestBody @Valid AlterarMinhaSenhaRequest request) {
        alterarMinhaSenhaUseCase.execute(new AlterarMinhaSenhaInput(
                usuarioId, request.senhaAtual(), request.novaSenha()));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{usuarioId}")
    public ResponseEntity<UsuarioPublicoDTO> buscarPorId(@PathVariable UUID usuarioId) {
        return ResponseEntity.ok(buscarUsuarioPublicoUseCase.execute(usuarioId));
    }
}
