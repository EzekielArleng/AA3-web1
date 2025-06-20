// src/main/java/com/gametester/controller/BugController.java
package br.ufscar.dc.dsw.com.gametester.controller;

import br.ufscar.dc.dsw.com.gametester.dto.BugCreateDTO;
import br.ufscar.dc.dsw.com.gametester.domain.Bug;
import br.ufscar.dc.dsw.com.gametester.domain.Usuario;
import br.ufscar.dc.dsw.com.gametester.service.BugService;
import br.ufscar.dc.dsw.com.gametester.service.SessaoTesteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/testador/sessoes/{sessaoId}/bugs")
public class BugController {

    private final BugService bugService;
    @Autowired private SessaoTesteService sessaoTesteService;

    @Autowired
    public BugController(BugService bugService) {
        this.bugService = bugService;
    }

    // Exibe a lista de bugs de uma sessão
    @GetMapping
    public String listarBugs(@PathVariable Long sessaoId, Model model) {
        List<Bug> bugs = bugService.listarBugsPorSessao(sessaoId);
        model.addAttribute("bugs", bugs);
        model.addAttribute("sessaoId", sessaoId);
        return "bug/lista"; // Renderiza o arquivo templates/bug/lista.html
    }

    // Exibe o formulário para criar um novo bug
    @GetMapping("/novo")
    public String mostrarFormularioDeCriacao(@PathVariable Long sessaoId, Model model) {
        model.addAttribute("sessao", sessaoTesteService.buscarPorId(sessaoId));
        model.addAttribute("bugDTO", new BugCreateDTO("", "", ""));
        return "testador/bug/formulario"; // Aponta para um formulário de bug específico
    }

    // Processa o envio do formulário de criação
    @PostMapping("/novo")
    public String criarBug(@PathVariable Long sessaoId,
                           @Valid @ModelAttribute("bugDTO") BugCreateDTO dto,
                           BindingResult result,
                           @AuthenticationPrincipal Usuario usuarioLogado,
                           RedirectAttributes redirectAttributes,
                           Model model) {

        if (result.hasErrors()) {
            model.addAttribute("sessao", sessaoTesteService.buscarPorId(sessaoId));
            return "testador/bug/formulario";
        }
        try {
            bugService.criarBug(dto, sessaoId, usuarioLogado);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Bug registrado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao registrar bug: " + e.getMessage());
        }
        return "redirect:/testador/sessoes/" + sessaoId;
    }

    // Deleta um bug
    @PostMapping("/{bugId}/delete")
    public String deletarBug(@PathVariable Integer sessaoId, @PathVariable Integer bugId, RedirectAttributes redirectAttributes) {
        bugService.deletarBug(bugId);
        redirectAttributes.addFlashAttribute("mensagem", "Bug excluído com sucesso!");
        return "redirect:/sessoes/" + sessaoId + "/bugs";
    }
}