package br.ufscar.dc.dsw.com.gametester.controller.api.admin;

import br.ufscar.dc.dsw.com.gametester.domain.Estrategia;
import br.ufscar.dc.dsw.com.gametester.dto.EstrategiaDTO; // DTO de entrada para criar/editar
import br.ufscar.dc.dsw.com.gametester.dto.EstrategiaResponseDTO; // DTO de saída
import br.ufscar.dc.dsw.com.gametester.service.EstrategiaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/estrategias")
public class AdminEstrategiaController {

    @Autowired
    private EstrategiaService estrategiaService;

    /**
     * READ (All) - Lista todas as estratégias.
     * Mapeado para: GET /api/admin/estrategias
     */
    @GetMapping
    public ResponseEntity<List<EstrategiaResponseDTO>> listarEstrategias() {
        List<Estrategia> estrategias = estrategiaService.listarTodas();
        List<EstrategiaResponseDTO> dtos = estrategias.stream()
                .map(EstrategiaResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * READ (One) - Busca uma estratégia pelo ID.
     * Mapeado para: GET /api/admin/estrategias/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<EstrategiaResponseDTO> buscarEstrategiaPorId(@PathVariable Integer id) {
        Estrategia estrategia = estrategiaService.buscarPorId(id);
        return ResponseEntity.ok(new EstrategiaResponseDTO(estrategia));
    }

    /**
     * CREATE - Cria uma nova estratégia.
     * Mapeado para: POST /api/admin/estrategias
     */
    @PostMapping
    public ResponseEntity<EstrategiaResponseDTO> criarEstrategia(@Valid @RequestBody EstrategiaDTO dto) {
        Estrategia novaEstrategia = estrategiaService.criarEstrategia(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(novaEstrategia.getId())
                .toUri();
        return ResponseEntity.created(location).body(new EstrategiaResponseDTO(novaEstrategia));
    }

    /**
     * UPDATE - Atualiza uma estratégia existente.
     * Mapeado para: PUT /api/admin/estrategias/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<EstrategiaResponseDTO> atualizarEstrategia(@PathVariable Integer id, @Valid @RequestBody EstrategiaDTO dto) {
        // Validação para garantir consistência entre o ID da URL e o do corpo
        if (!id.equals(dto.id())) {
            // throw new InvalidDataException("Conflito de IDs na requisição de atualização.");
        }
        Estrategia estrategiaAtualizada = estrategiaService.editarEstrategia(dto);
        return ResponseEntity.ok(new EstrategiaResponseDTO(estrategiaAtualizada));
    }

    /**
     * DELETE - Exclui uma estratégia.
     * Mapeado para: DELETE /api/admin/estrategias/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirEstrategia(@PathVariable Integer id) {
        estrategiaService.excluirEstrategia(id);
        return ResponseEntity.noContent().build();
    }
}