package com.henrique.ifconecta.infrastructure.web.usuario.dto;

import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegistrarAlunoRequest(
    UUID cursoId,

    @NotBlank(message = "O nome é obrigatório.")
    String nome,

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "Formato de e-mail inválido.")
    String email,

    @NotBlank(message = "A senha é obrigatória.")
    @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres.")
    String password,

    @NotBlank(message = "O prontuário é obrigatório.")
    @Pattern(regexp = "^[Ss][Ll]\\d{6}[A-Za-z]$", message = "O prontuário deve seguir o formato SL000000X (SL + 6 dígitos + 1 letra).")
    String prontuario
) {

}
