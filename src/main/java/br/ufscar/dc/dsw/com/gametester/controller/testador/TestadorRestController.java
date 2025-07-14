package br.ufscar.dc.dsw.com.gametester.controller.testador;

import br.ufscar.dc.dsw.com.gametester.domain.Projeto;
import br.ufscar.dc.dsw.com.gametester.domain.SessaoTeste;
import br.ufscar.dc.dsw.com.gametester.domain.Usuario;
import br.ufscar.dc.dsw.com.gametester.dto.ProjetoResponseDTO;
import br.ufscar.dc.dsw.com.gametester.dto.SessaoResponseDTO;
import br.ufscar.dc.dsw.com.gametester.service.ProjetoService;
import br.ufscar.dc.dsw.com.gametester.service.SessaoTesteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/testador")
public class TestadorRestController {

    @Autowired
    private ProjetoService projetoService;

    @Autowired
    private SessaoTesteService sessaoTesteService;

    /**
     * Retorna a lista de projetos associados ao testador logado.
     * Mapeado para: GET /api/testador/projetos
     */
    @GetMapping("/projetos")
    public ResponseEntity<List<ProjetoResponseDTO>> listarMeusProjetos(
            @AuthenticationPrincipal Usuario usuarioLogado,
            // O Pageable é a forma padrão e mais robusta do Spring para lidar com ordenação e paginação.
            // O cliente pode usar: /api/testador/projetos?sort=nome,desc
            @PageableDefault(sort = "nome", direction = Sort.Direction.ASC) Pageable pageable) {

        List<Projeto> projetos = projetoService.listarProjetosDoUsuario(usuarioLogado, pageable);
        List<ProjetoResponseDTO> dtos = projetos.stream()
                .map(ProjetoResponseDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }


    /**
     * Retorna as sessões de um projeto específico ao qual o testador pertence.
     * Mapeado para: GET /api/testador/projetos/{projetoId}/sessoes
     */
    @GetMapping("/projetos/{projetoId}/sessoes")
    public ResponseEntity<List<SessaoResponseDTO>> listarSessoesPorProjeto(
            @PathVariable Integer projetoId,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        // A lógica de serviço deve garantir que o testador logado pertence a este projeto.
        List<SessaoTeste> sessoes = sessaoTesteService.listarSessoesPorProjeto(projetoId, usuarioLogado);
        List<SessaoResponseDTO> dtos = sessoes.stream()
                .map(SessaoResponseDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }
}