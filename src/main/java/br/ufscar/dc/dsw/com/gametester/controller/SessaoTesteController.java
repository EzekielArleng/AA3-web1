package br.ufscar.dc.dsw.com.gametester.controller;

import br.ufscar.dc.dsw.com.gametester.domain.SessaoTeste;
import br.ufscar.dc.dsw.com.gametester.domain.Usuario;
import br.ufscar.dc.dsw.com.gametester.dto.SessaoCreateDTO;
import br.ufscar.dc.dsw.com.gametester.dto.SessaoResponseDTO;
import br.ufscar.dc.dsw.com.gametester.service.SessaoTesteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/testador/sessoes")
public class SessaoTesteController {

    @Autowired
    private SessaoTesteService sessaoTesteService;

    /**
     * READ (All) - Lista todas as sessões do testador logado.
     */
    @GetMapping
    public ResponseEntity<List<SessaoResponseDTO>> listarMinhasSessoes(
            @AuthenticationPrincipal Usuario testador,
            @PageableDefault(sort = "dataHoraCriacao", direction = Sort.Direction.DESC) Pageable pageable) {

        List<SessaoTeste> sessoes = sessaoTesteService.listarSessoesPorTestador(testador, pageable);
        List<SessaoResponseDTO> dtos = sessoes.stream().map(SessaoResponseDTO::new).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * READ (One) - Busca os detalhes de uma sessão específica.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SessaoResponseDTO> visualizarSessao(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        SessaoTeste sessao = sessaoTesteService.buscarSessaoParaVisualizacao(id, usuarioLogado);
        return ResponseEntity.ok(new SessaoResponseDTO(sessao));
    }

    /**
     * CREATE - Cria uma nova sessão de teste.
     */
    @PostMapping
    public ResponseEntity<SessaoResponseDTO> criarSessao(
            @Valid @RequestBody SessaoCreateDTO dto,
            @AuthenticationPrincipal Usuario testador) {

        SessaoTeste novaSessao = sessaoTesteService.criarSessao(dto, testador);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(novaSessao.getId()).toUri();
        return ResponseEntity.created(location).body(new SessaoResponseDTO(novaSessao));
    }

    /**
     * ACTION: Inicia uma sessão de teste.
     */
    @PostMapping("/{id}/iniciar")
    public ResponseEntity<SessaoResponseDTO> iniciarSessao(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        SessaoTeste sessaoIniciada = sessaoTesteService.iniciarSessao(id, usuarioLogado);
        return ResponseEntity.ok(new SessaoResponseDTO(sessaoIniciada));
    }

    /**
     * ACTION: Finaliza uma sessão de teste.
     */
    @PostMapping("/{id}/finalizar")
    public ResponseEntity<SessaoResponseDTO> finalizarSessao(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        SessaoTeste sessaoFinalizada = sessaoTesteService.finalizarSessao(id, usuarioLogado);
        return ResponseEntity.ok(new SessaoResponseDTO(sessaoFinalizada));
    }
}