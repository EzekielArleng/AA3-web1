package br.ufscar.dc.dsw.com.gametester.dto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotBlank;

public record SessaoCreateDTO(
        @NotNull(message = "O projeto é obrigatório.")
        Integer projetoId,

        @NotNull(message = "A estratégia é obrigatória.")
        Integer estrategiaId,

        @NotNull(message = "O tempo da sessão é obrigatório.")
        @Min(value = 1, message = "O tempo da sessão deve ser de no mínimo 1 minuto.")
        int tempoSessaoMinutos,

        String descricao
) {}