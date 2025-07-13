package br.ufscar.dc.dsw.com.gametester.dto;

import br.ufscar.dc.dsw.com.gametester.domain.Projeto;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public record ProjetoResponseDTO(
        Integer id,
        String nome,
        String descricao,
        LocalDateTime dataCriacao,
        List<UsuarioResponseDTO> membros // Usa o DTO de Usuário para a lista de membros
) {
    /**
     * Construtor de conveniência para mapear a entidade Projeto para este DTO.
     */
    public ProjetoResponseDTO(Projeto projeto) {
        this(
                projeto.getId(),
                projeto.getNome(),
                projeto.getDescricao(),
                projeto.getDataCriacao(),
                // Mapeamento seguro da coleção de membros
                projeto.getMembros() != null ?
                        projeto.getMembros().stream()
                                .map(UsuarioResponseDTO::new) // Converte cada Usuario para UsuarioResponseDTO
                                .collect(Collectors.toList()) :
                        Collections.emptyList()
        );
    }
}