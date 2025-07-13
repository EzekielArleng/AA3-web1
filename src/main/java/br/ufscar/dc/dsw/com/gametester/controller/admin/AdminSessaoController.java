package br.ufscar.dc.dsw.com.gametester.controller.admin;

import br.ufscar.dc.dsw.com.gametester.exception.InvalidDataException;
import br.ufscar.dc.dsw.com.gametester.domain.SessaoTeste;
import br.ufscar.dc.dsw.com.gametester.dto.AdminSessaoEditDTO;
import br.ufscar.dc.dsw.com.gametester.dto.SessaoResponseDTO;
import br.ufscar.dc.dsw.com.gametester.service.EstrategiaService;
import br.ufscar.dc.dsw.com.gametester.service.ProjetoService;
import br.ufscar.dc.dsw.com.gametester.service.SessaoTesteService;
import br.ufscar.dc.dsw.com.gametester.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/sessoes")
public class AdminSessaoController {

    @Autowired private SessaoTesteService sessaoTesteService;
    // Injetando os outros serviços necessários para popular os formulários
    @Autowired private ProjetoService projetoService;
    @Autowired private UsuarioService usuarioService;
    @Autowired private EstrategiaService estrategiaService;

    /**
     * Exibe a lista de todas as sessões de teste no sistema.
     */

    @GetMapping
    public ResponseEntity<List<SessaoResponseDTO>> listarTodasSessoes(Model model){
        List<SessaoTeste> sessoes = sessaoTesteService.listarTodasAsSessoes();
        List<SessaoResponseDTO> dtos = sessoes.stream().map(SessaoResponseDTO::new).toList();
        return ResponseEntity.ok(dtos);
    }

// Dentro de um @RestController, por exemplo, SessaoRestController.java

    @GetMapping("/{id}")
    public ResponseEntity<SessaoResponseDTO> buscarSessaoPorId(@PathVariable Long id) {
        SessaoTeste sessao = sessaoTesteService.buscarPorId(id);

        // O construtor do SessaoResponseDTO já faz todo o mapeamento complexo
        // que você estava fazendo manualmente no seu método MVC.
        return ResponseEntity.ok(new SessaoResponseDTO(sessao));
    }


    @PutMapping("/{id}")
    public ResponseEntity<SessaoResponseDTO> atualizarSessao(@PathVariable Long id, @Valid @RequestBody AdminSessaoEditDTO dto){

        if (!id.equals(dto.id())) {
            throw new InvalidDataException("O ID fornecido na URL (" + id +
                    ") não corresponde ao ID no corpo da requisição (" + dto.id() + ").");
        }

        SessaoTeste sessaoAtualizada = sessaoTesteService.editarSessaoPorAdmin(dto);
        return ResponseEntity.ok(new SessaoResponseDTO(sessaoAtualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirSessao(@PathVariable Long id) {
        sessaoTesteService.excluirSessao(id);
        return ResponseEntity.noContent().build();
    }
}