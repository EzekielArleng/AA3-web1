package br.ufscar.dc.dsw.com.gametester.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "O e-mail fornecido é inválido.")
        String email,

        @NotBlank(message = "A senha é obrigatória.")
        String senha
) {
}