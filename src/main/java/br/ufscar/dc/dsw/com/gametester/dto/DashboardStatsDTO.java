package br.ufscar.dc.dsw.com.gametester.dto;

// DTO para carregar os dados agregados do dashboard
public record DashboardStatsDTO(
        long totalUsuarios,
        long totalProjetos,
        long sessoesEsteMes
) {}