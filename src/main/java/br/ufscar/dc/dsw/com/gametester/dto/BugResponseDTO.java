package br.ufscar.dc.dsw.com.gametester.dto;

import br.ufscar.dc.dsw.com.gametester.domain.Bug;
import br.ufscar.dc.dsw.com.gametester.domain.SessaoTeste;
import br.ufscar.dc.dsw.com.gametester.domain.enums.Severidade;
import jakarta.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public record BugResponseDTO(
    Integer id,
    Long sessaoTesteId,
    String descricao,
    Severidade severidade,
    LocalDateTime dataRegistro,
    String screenshotUrl

) {
    public BugResponseDTO(Bug bug) {
        this(
                bug.getId(),
                bug.getSessaoTeste().getId(), // Pega apenas o ID da sessão associada
                bug.getDescricao(),
                bug.getSeveridade(),
                bug.getDataRegistro().toLocalDateTime(), // Converte o Timestamp para LocalDateTime
                bug.getScreenshotUrl()
        );
    }

}
