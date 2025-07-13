package br.ufscar.dc.dsw.com.gametester.dto;

import jakarta.validation.constraints.NotNull;

public record MembroRequestDTO(
        @NotNull Long usuarioId
) {}