package br.ufscar.dc.dsw.com.gametester.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EstrategiaDTO(
        Integer id,

        @NotBlank(message = "O nome da estratégia é obrigatório.")
        @Size(min = 5, message = "O nome deve ter no mínimo 5 caracteres.")
        String nome,

        @NotBlank(message = "A descrição é obrigatória.")
        String descricao,

        String exemplos,
        String dicas,
        String imagemPath // Campo para o path da imagem
) {}