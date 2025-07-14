package br.ufscar.dc.dsw.com.gametester.controller.admin; // Sugestão: mover para um pacote /api

import br.ufscar.dc.dsw.com.gametester.domain.Usuario;
import br.ufscar.dc.dsw.com.gametester.dto.AdminUsuarioCreateDTO;
import br.ufscar.dc.dsw.com.gametester.dto.AdminUsuarioEditDTO;
import br.ufscar.dc.dsw.com.gametester.dto.UsuarioResponseDTO; // Importando o novo DTO
import br.ufscar.dc.dsw.com.gametester.exception.InvalidDataException;
import br.ufscar.dc.dsw.com.gametester.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/usuarios") // Convenção: usar /api para endpoints REST
public class AdminUsuarioRestController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Retorna uma lista de todos os usuários.
     * Mapeado para: GET /api/admin/usuarios
     */
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarUsuarios() {
        List<Usuario> usuarios = usuarioService.listarTodos();
        // Mapeia a lista de Entidades para uma lista de DTOs de resposta
        List<UsuarioResponseDTO> dtos = usuarios.stream()
                .map(UsuarioResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Busca um usuário específico pelo seu ID.
     * Mapeado para: GET /api/admin/usuarios/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarUsuarioPorId(@PathVariable Long id) {
        Usuario usuario = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(new UsuarioResponseDTO(usuario));
    }


    /**
     * Cria um novo usuário.
     * Mapeado para: POST /api/admin/usuarios
     */
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> criarUsuario(@Valid @RequestBody AdminUsuarioCreateDTO dto) {
        Usuario novoUsuario = usuarioService.criarUsuarioPorAdmin(dto);

        // Constrói a URI do novo recurso criado para retornar no header "Location"
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(novoUsuario.getId())
                .toUri();

        // Retorna status 201 Created com a localização e o corpo do novo usuário
        return ResponseEntity.created(location).body(new UsuarioResponseDTO(novoUsuario));
    }

    /**
     * Atualiza um usuário existente.
     * Mapeado para: PUT /api/admin/usuarios/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizarUsuario(@PathVariable Long id, @Valid @RequestBody AdminUsuarioEditDTO dto) {
        // Garante que o ID do DTO seja o mesmo da URL para consistência
        if (!id.equals(dto.id())) {
            throw new InvalidDataException("O ID na URL não corresponde ao ID no corpo da requisição.");
        }
        Usuario usuarioAtualizado = usuarioService.editarUsuarioPorAdmin(dto);
        return ResponseEntity.ok(new UsuarioResponseDTO(usuarioAtualizado));
    }


    /**
     * Exclui um usuário.
     * Mapeado para: DELETE /api/admin/usuarios/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirUsuario(@PathVariable Long id, @AuthenticationPrincipal Usuario adminLogado) {
        usuarioService.excluirUsuario(id, adminLogado);
        // Retorna status 204 No Content, indicando sucesso sem corpo de resposta
        return ResponseEntity.noContent().build();
    }
}