package br.ufscar.dc.dsw.com.gametester.dto;
import jakarta.validation.constraints.NotBlank;

public record BugCreateDTO(
        @NotBlank(message = "A descrição do bug é obrigatória.")
        String descricao,

        @NotBlank(message = "A severidade do bug é obrigatória.")
        String severidade,

        String screenshotUrl
) {}