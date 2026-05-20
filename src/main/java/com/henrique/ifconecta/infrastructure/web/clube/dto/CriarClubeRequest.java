package com.henrique.ifconecta.infrastructure.web.clube.dto;

import jakarta.validation.constraints.NotBlank;

public record CriarClubeRequest(
        @NotBlank(message = "O nome do clube é obrigatório.") 
        String nome,

        @NotBlank(message = "A descrição do clube é obrigatória.")
        String descricao
) {

}
