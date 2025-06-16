package br.ufscar.dc.dsw.com.gametester.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SenhaChangeDTO(
        @NotBlank(message = "A senha atual é obrigatória.")
        String senhaAtual,

        @NotBlank(message = "A nova senha é obrigatória.")
        @Size(min = 6, message = "A nova senha deve ter no mínimo 6 caracteres.")
        String novaSenha,

        @NotBlank(message = "A confirmação da senha é obrigatória.")
        String confirmaNovaSenha
) {}

// Nota: A validação para checar se 'novaSenha' e 'confirmaNovaSenha' são iguais
// pode ser feita no serviço ou com uma anotação de validação customizada a nível de classe.