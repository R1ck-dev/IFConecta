package com.henrique.ifconecta.infrastructure.web.clube.dto;

import com.henrique.ifconecta.domain.clube.enums.TipoAcesso;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CriarClubeRequest(
    @Schema(description = "Nome único do clube", example = "Clube de Programação Competitiva")
    @NotBlank(message = "O nome do clube é obrigatório.")
    String nome,
    
    @Schema(description = "Descrição detalhada do objetivo do clube", example = "Grupo focado em resolver problemas do LeetCode e Beecrowd.")
    @NotBlank(message = "A descrição do clube é obrigatória.")
    String descricao,
    
    @Schema(description = "Define quem pode ver o conteúdo do clube", example = "PUBLICO")
    @NotNull(message = "O tipo de acesso é obrigatório.")
    TipoAcesso tipoAcesso
) {
    
}
