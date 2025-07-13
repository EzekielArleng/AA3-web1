package br.ufscar.dc.dsw.com.gametester.dto;

import br.ufscar.dc.dsw.com.gametester.domain.SessaoTeste;
import br.ufscar.dc.dsw.com.gametester.domain.enums.StatusSessao;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public record SessaoResponseDTO (
        Long id,

        Long projeto_id,

        Integer testador_id,

        Integer estrategia_id,

        int tempoSessaoMinutos,

        String descricao,
        StatusSessao status,
        LocalDateTime dataHoraInicio,
        LocalDateTime dataHoraFim,
        List<BugResponseDTO> bugs
)
{
    public SessaoResponseDTO(SessaoTeste sessao){

        this(
                sessao.getId(),
                sessao.getTestador().getId(),
                sessao.getProjeto().getId(),
                sessao.getEstrategia().getId(),
                sessao.getTempoSessaoMinutos(),
                sessao.getDescricao(),
                sessao.getStatus(),
                sessao.getDataHoraInicio(),
                sessao.getDataHoraFim(),

                sessao.getBugs() != null ?
                        sessao.getBugs().stream().map(BugResponseDTO::new).collect(Collectors.toList()) :
                        Collections.emptyList()
        );
    }
}
