package br.ufscar.dc.dsw.com.gametester.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProjetoDTO(
        Integer id, // Será nulo na criação, preenchido na edição

        @NotBlank(message = "O nome do projeto é obrigatório.")
        @Size(min = 3, message = "O nome do projeto deve ter no mínimo 3 caracteres.")
        String nome,
        String descricao
) {}