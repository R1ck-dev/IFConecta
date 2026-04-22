package com.henrique.ifconecta.application.usuario.dto;

public record RegistrarAlunoInput(
    String nome,
    String email,
    String password,
    String prontuario
) {
} 
