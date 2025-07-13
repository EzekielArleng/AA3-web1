package br.ufscar.dc.dsw.com.gametester.dto;

/**
 * DTO (Data Transfer Object) para a resposta de uma requisição de login bem-sucedida.
 * Ele contém o token de acesso que o cliente usará para autenticar requisições futuras.
 *
 * @param token O token de autenticação gerado (geralmente um JWT).
 */
public record LoginResponseDTO(String token) {
}