package br.ufscar.dc.dsw.com.gametester.controller;

import br.ufscar.dc.dsw.com.gametester.domain.Bug;
import br.ufscar.dc.dsw.com.gametester.domain.Usuario;
import br.ufscar.dc.dsw.com.gametester.dto.BugCreateDTO;
import br.ufscar.dc.dsw.com.gametester.dto.BugResponseDTO;
import br.ufscar.dc.dsw.com.gametester.service.BugService;
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
// A URI aninhada é uma ótima prática REST para representar hierarquia
@RequestMapping("/api/testador/sessoes/{sessaoId}/bugs")
public class BugRestController {

    @Autowired
    private BugService bugService;

    /**
     * Lista todos os bugs de uma sessão específica.
     * Mapeado para: GET /api/testador/sessoes/{sessaoId}/bugs
     */
    @GetMapping
    public ResponseEntity<List<BugResponseDTO>> listarBugsPorSessao(@PathVariable Long sessaoId) {
        List<Bug> bugs = bugService.listarBugsPorSessao(sessaoId);

        List<BugResponseDTO> dtos = bugs.stream()
                .map(BugResponseDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    /**
     * Busca um bug específico pelo seu ID.
     * Mapeado para: GET /api/testador/sessoes/{sessaoId}/bugs/{bugId}
     */
    @GetMapping("/{bugId}")
    public ResponseEntity<BugResponseDTO> buscarBugPorId(@PathVariable Long sessaoId, @PathVariable Integer bugId) {
        // A lógica de serviço deve garantir que o bug pertence à sessão
        Bug bug = bugService.buscarPorId(bugId);
        return ResponseEntity.ok(new BugResponseDTO(bug));
    }

    /**
     * Registra um novo bug em uma sessão específica.
     * Mapeado para: POST /api/testador/sessoes/{sessaoId}/bugs
     */
    @PostMapping
    public ResponseEntity<BugResponseDTO> criarBugNaSessao(@PathVariable Long sessaoId,
                                                           @Valid @RequestBody BugCreateDTO dto,
                                                           @AuthenticationPrincipal Usuario usuarioLogado) {

        Bug novoBug = bugService.criarBug(dto, sessaoId, usuarioLogado);

        // Constrói a URI do novo recurso criado para o header "Location"
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(novoBug.getId())
                .toUri();

        return ResponseEntity.created(location).body(new BugResponseDTO(novoBug));
    }

    /**
     * Deleta um bug específico.
     * Mapeado para: DELETE /api/testador/sessoes/{sessaoId}/bugs/{bugId}
     */
    @DeleteMapping("/{bugId}")
    public ResponseEntity<Void> deletarBug(@PathVariable Long sessaoId,
                                           @PathVariable Integer bugId,
                                           @AuthenticationPrincipal Usuario usuarioLogado) {

        // A lógica de serviço deve verificar se o 'usuarioLogado' tem permissão para deletar este bug
        bugService.deletarBug(bugId, usuarioLogado);
        return ResponseEntity.noContent().build();
    }
}