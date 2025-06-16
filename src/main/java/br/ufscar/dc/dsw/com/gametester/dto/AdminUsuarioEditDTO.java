package br.ufscar.dc.dsw.com.gametester.dto;

import br.ufscar.dc.dsw.com.gametester.model.TipoPerfil;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// A senha é opcional na edição, então não validamos aqui.
public record AdminUsuarioEditDTO(
        Long id,

        @NotBlank(message = "O nome é obrigatório.")
        String nome,

        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "Formato de e-mail inválido.")
        String email,

        String novaSenha,

        String confirmaNovaSenha,

        @NotNull(message = "O tipo de perfil é obrigatório.")
        TipoPerfil tipoPerfil
) {}