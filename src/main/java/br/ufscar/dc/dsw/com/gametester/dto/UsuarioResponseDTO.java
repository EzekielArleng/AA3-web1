package br.ufscar.dc.dsw.com.gametester.dto;

import br.ufscar.dc.dsw.com.gametester.domain.Usuario;
import br.ufscar.dc.dsw.com.gametester.domain.enums.TipoPerfil;

// DTO para enviar dados de usuário de volta ao cliente, sem a senha.
public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email,
        TipoPerfil tipoPerfil
) {
    // Construtor de conveniência para mapear da entidade para o DTO
    public UsuarioResponseDTO(Usuario usuario) {
        this(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getTipoPerfil());
    }
}