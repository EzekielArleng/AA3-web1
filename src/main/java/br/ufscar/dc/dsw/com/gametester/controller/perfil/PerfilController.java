package br.ufscar.dc.dsw.com.gametester.controller.api.perfil;

import br.ufscar.dc.dsw.com.gametester.domain.Usuario;
import br.ufscar.dc.dsw.com.gametester.dto.PerfilEditDTO;
import br.ufscar.dc.dsw.com.gametester.dto.SenhaChangeDTO;
import br.ufscar.dc.dsw.com.gametester.dto.UsuarioResponseDTO;
import br.ufscar.dc.dsw.com.gametester.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/perfil") // Recurso que representa o usuário autenticado
public class PerfilController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Retorna os dados do perfil do usuário atualmente logado.
     * Mapeado para: GET /api/perfil
     */
    @GetMapping
    public ResponseEntity<UsuarioResponseDTO> obterMeuPerfil(@AuthenticationPrincipal Usuario usuarioLogado) {
        // O objeto 'usuarioLogado' já foi carregado pelo nosso SecurityFilter.
        // Apenas precisamos convertê-lo para o DTO de resposta.
        return ResponseEntity.ok(new UsuarioResponseDTO(usuarioLogado));
    }

    /**
     * Atualiza os dados do perfil (nome, e-mail) do usuário logado.
     * Mapeado para: PUT /api/perfil
     */
    @PutMapping
    public ResponseEntity<UsuarioResponseDTO> atualizarMeuPerfil(
            @AuthenticationPrincipal Usuario usuarioLogado,
            @Valid @RequestBody PerfilEditDTO dto) {

        // A lógica de negócio permanece no serviço.
        // Ajuste o serviço para retornar o usuário atualizado para maior clareza.
        Usuario usuarioAtualizado = usuarioService.atualizarPerfil(usuarioLogado, dto);

        // Retorna 200 OK com os dados atualizados do usuário.
        return ResponseEntity.ok(new UsuarioResponseDTO(usuarioAtualizado));
    }

    /**
     * Altera a senha do usuário logado.
     * Mapeado para: PUT /api/perfil/senha
     */
    @PutMapping("/senha")
    public ResponseEntity<Void> alterarMinhaSenha(
            @AuthenticationPrincipal Usuario usuarioLogado,
            @Valid @RequestBody SenhaChangeDTO dto) {

        // A lógica de negócio (verificar senha atual, etc.) está no serviço.
        usuarioService.alterarSenha(usuarioLogado, dto);

        // Retorna 204 No Content para indicar sucesso sem corpo de resposta.
        return ResponseEntity.noContent().build();
    }
}