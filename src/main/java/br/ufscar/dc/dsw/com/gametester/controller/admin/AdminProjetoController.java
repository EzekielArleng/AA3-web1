package br.ufscar.dc.dsw.com.gametester.controller.admin;

import br.ufscar.dc.dsw.com.gametester.domain.Projeto;
import br.ufscar.dc.dsw.com.gametester.domain.Usuario;
import br.ufscar.dc.dsw.com.gametester.dto.MembroRequestDTO;
import br.ufscar.dc.dsw.com.gametester.dto.ProjetoDTO;
import br.ufscar.dc.dsw.com.gametester.dto.ProjetoResponseDTO;
import br.ufscar.dc.dsw.com.gametester.dto.UsuarioResponseDTO;
import br.ufscar.dc.dsw.com.gametester.service.ProjetoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/projetos")
public class AdminProjetoController {

    @Autowired
    private ProjetoService projetoService;

    // ============== ENDPOINTS PARA PROJETOS ==============

    @GetMapping
    public ResponseEntity<List<ProjetoResponseDTO>> listarProjetos() {
        List<Projeto> projetos = projetoService.listarTodos(null); // A lógica de ordenação pode ser passada via Pageable
        List<ProjetoResponseDTO> dtos = projetos.stream().map(ProjetoResponseDTO::new).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjetoResponseDTO> buscarProjetoPorId(@PathVariable Integer id) {
        Projeto projeto = projetoService.buscarPorId(id);
        return ResponseEntity.ok(new ProjetoResponseDTO(projeto));
    }

    @PostMapping
    public ResponseEntity<ProjetoResponseDTO> criarProjeto(@Valid @RequestBody ProjetoDTO dto) {
        Projeto novoProjeto = projetoService.criarProjeto(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(novoProjeto.getId()).toUri();
        return ResponseEntity.created(location).body(new ProjetoResponseDTO(novoProjeto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjetoResponseDTO> atualizarProjeto(@PathVariable Integer id, @Valid @RequestBody ProjetoDTO dto) {
        Projeto projetoAtualizado = projetoService.editarProjeto(dto);
        return ResponseEntity.ok(new ProjetoResponseDTO(projetoAtualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirProjeto(@PathVariable Integer id) {
        projetoService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    // ============== ENDPOINTS PARA MEMBROS (RECURSO ANINHADO) ==============

    @GetMapping("/{projetoId}/membros")
    public ResponseEntity<List<UsuarioResponseDTO>> listarMembros(@PathVariable Integer projetoId) {
        List<Usuario> membros = projetoService.listarMembrosDoProjeto(projetoId);
        List<UsuarioResponseDTO> dtos = membros.stream().map(UsuarioResponseDTO::new).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{projetoId}/membros/{usuarioId}")
    public ResponseEntity<Void> adicionarMembro(@PathVariable Integer projetoId, @PathVariable Long usuarioId) {
        projetoService.adicionarMembro(projetoId, usuarioId);
        return ResponseEntity.ok().build(); // Retorna 200 OK para indicar sucesso na adição/associação
    }

    @DeleteMapping("/{projetoId}/membros/{usuarioId}")
    public ResponseEntity<Void> removerMembro(@PathVariable Integer projetoId, @PathVariable Long usuarioId) {
        projetoService.removerMembro(projetoId, usuarioId);
        return ResponseEntity.noContent().build(); // Retorna 204 para indicar sucesso na remoção
    }
}