package br.ufscar.dc.dsw.com.gametester.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO (Data Transfer Object) para os dados de uma requisição de login.
 * Representa o corpo JSON que o cliente envia para o endpoint de autenticação.
 *
 * @param email O e-mail do usuário que está tentando fazer login.
 * @param senha A senha em texto plano do usuário.
 */
public record LoginRequestDTO(
        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "O e-mail fornecido é inválido.")
        String email,

        @NotBlank(message = "A senha é obrigatória.")
        String senha
) {
}