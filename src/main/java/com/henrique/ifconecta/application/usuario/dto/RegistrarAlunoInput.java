package com.henrique.ifconecta.application.usuario.dto;

import java.util.UUID;

public record RegistrarAlunoInput(
    UUID cursoId,
    String nome,
    String email,
    String password,
    String prontuario
) {
} 
