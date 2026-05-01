package com.henrique.ifconecta.infrastructure.web.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ConvidarInstitucionalRequest(
    @NotBlank(message = "O nome é obrigatório.")
    String nome,
    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "Formato de e-mail inválido.")
    String emailAcad,
    @NotBlank(message = "O setor é obrigatório.")
    String setor,
    @NotBlank(message = "O cargo é obrigatório.")
    String cargo
) {
} 
