package br.ufscar.dc.dsw.com.gametester.dto;

import br.ufscar.dc.dsw.com.gametester.model.StatusSessao;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

public record AdminSessaoEditDTO(
        Long id,

        @NotNull(message = "O projeto é obrigatório.")
        Integer projetoId,

        @NotNull(message = "O testador é obrigatório.")
        Long testadorId,

        @NotNull(message = "A estratégia é obrigatória.")
        Integer estrategiaId,

        @NotNull(message = "O tempo da sessão é obrigatório.")
        @Min(value = 1, message = "O tempo da sessão deve ser de no mínimo 1 minuto.")
        int tempoSessaoMinutos,

        String descricao,

        @NotNull(message = "O status é obrigatório.")
        StatusSessao status,

        // Anotação para ajudar o Spring a converter a string do formulário para LocalDateTime
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime dataHoraInicio,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime dataHoraFim
) {}