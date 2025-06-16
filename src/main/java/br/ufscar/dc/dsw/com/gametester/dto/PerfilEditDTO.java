package br.ufscar.dc.dsw.com.gametester.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PerfilEditDTO(
        @NotBlank(message = "O nome é obrigatório.")
        @Size(min = 3, message = "O nome deve ter no mínimo 3 caracteres.")
        String nome,

        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "O formato do e-mail é inválido.")
        String email
) {}