package com.henrique.ifconecta.infrastructure.web.clube.dto;

import com.henrique.ifconecta.domain.clube.enums.TipoAcesso;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CriarClubeRequest(
    @NotBlank(message = "O nome do clube é obrigatório.")
    String nome,
    
    @NotBlank(message = "A descrição do clube é obrigatória.")
    String descricao,
    
    @NotNull(message = "O tipo de acesso é obrigatório.")
    TipoAcesso tipoAcesso
) {
    
}
