package br.ufscar.dc.dsw.com.gametester.dto;

import br.ufscar.dc.dsw.com.gametester.domain.Estrategia;

public record EstrategiaResponseDTO(
        Integer id,
        String nome,
        String descricao,
        String exemplos,
        String dicas
) {
    public EstrategiaResponseDTO(Estrategia estrategia){
        this(
                estrategia.getId(),
                estrategia.getNome(),
                estrategia.getDescricao(),
                estrategia.getExemplos(),
                estrategia.getDicas()
        );
    }
}
