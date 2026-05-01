package com.henrique.ifconecta.infrastructure.web.usuario.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.henrique.ifconecta.application.usuario.dto.ConvidarInstitucionalInput;
import com.henrique.ifconecta.application.usuario.dto.ConvidarProfessorInput;
import com.henrique.ifconecta.application.usuario.usecase.ConvidarInstitucionalUseCase;
import com.henrique.ifconecta.application.usuario.usecase.ConvidarProfessorUseCase;
import com.henrique.ifconecta.infrastructure.web.usuario.dto.ConvidarInstitucionalRequest;
import com.henrique.ifconecta.infrastructure.web.usuario.dto.ConvidarProfessorRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/usuarios")
@RequiredArgsConstructor
@Tag(name = "Admin - Usuários", description = "Operações exclusivas para administradores (Convites)")
public class AdminUsuarioController {

    private final ConvidarProfessorUseCase convidarProfessorUseCase;
    private final ConvidarInstitucionalUseCase convidarInstitucionalUseCase;

    @Operation(summary = "Convidar Professor", description = "Gera uma conta para um professor e envia o link de ativação.")
    @ApiResponse(responseCode = "201", description = "Convite enviado com sucesso")
    @ApiResponse(responseCode = "400", description = "E-mail inválido ou já cadastrado")
    @PostMapping("/professores/convidar")
    public ResponseEntity<Void> convidarProfessor(@RequestBody @Valid ConvidarProfessorRequest request) {
        convidarProfessorUseCase.execute(new ConvidarProfessorInput(
                request.nome(),
                request.emailAcad(),
                request.siape()));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Convidar Institucional", description = "Gera uma conta para um membro institucional (ex: TI, Direção) e envia o link de ativação.")
    @ApiResponse(responseCode = "201", description = "Convite enviado com sucesso")
    @ApiResponse(responseCode = "400", description = "E-mail inválido ou já cadastrado")
    @PostMapping("/institucionais/convidar")
    public ResponseEntity<Void> convidarInstitucional(@RequestBody @Valid ConvidarInstitucionalRequest request) {
        convidarInstitucionalUseCase.execute(new ConvidarInstitucionalInput(
                request.nome(),
                request.emailAcad(),
                request.setor(),
                request.cargo()));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
