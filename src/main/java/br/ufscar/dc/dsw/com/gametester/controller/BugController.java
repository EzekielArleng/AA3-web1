// src/main/java/com/gametester/controller/BugController.java
package br.ufscar.dc.dsw.com.gametester.controller;

import br.ufscar.dc.dsw.com.gametester.dto.BugCreateDTO;
import br.ufscar.dc.dsw.com.gametester.model.Bug;
import br.ufscar.dc.dsw.com.gametester.model.Usuario;
import br.ufscar.dc.dsw.com.gametester.service.BugService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/sessoes/{sessaoId}/bugs") // Rotas aninhadas para bugs dentro de uma sessão
public class BugController {

    private final BugService bugService;

    @Autowired
    public BugController(BugService bugService) {
        this.bugService = bugService;
    }

    // Exibe a lista de bugs de uma sessão
    @GetMapping
    public String listarBugs(@PathVariable Integer sessaoId, Model model) {
        List<Bug> bugs = bugService.listarBugsPorSessao(sessaoId);
        model.addAttribute("bugs", bugs);
        model.addAttribute("sessaoId", sessaoId);
        return "bug/lista"; // Renderiza o arquivo templates/bug/lista.html
    }

    // Exibe o formulário para criar um novo bug
    @GetMapping("/novo")
    public String mostrarFormularioDeCriacao(@PathVariable Integer sessaoId, Model model) {
        model.addAttribute("bug", new Bug());
        model.addAttribute("sessaoId", sessaoId);
        return "bug/formulario"; // Renderiza o arquivo templates/bug/formulario.html
    }

    // Processa o envio do formulário de criação
    @PostMapping
    public String criarBug(@PathVariable Long sessaoId, BugCreateDTO bug, @AuthenticationPrincipal Usuario usuarioLogado, RedirectAttributes redirectAttributes) {
        bugService.criarBug(bug, sessaoId, usuarioLogado);
        redirectAttributes.addFlashAttribute("mensagem", "Bug registrado com sucesso!");
        return "redirect:/sessoes/" + sessaoId + "/bugs";
    }

    // Deleta um bug
    @PostMapping("/{bugId}/delete")
    public String deletarBug(@PathVariable Integer sessaoId, @PathVariable Integer bugId, RedirectAttributes redirectAttributes) {
        bugService.deletarBug(bugId);
        redirectAttributes.addFlashAttribute("mensagem", "Bug excluído com sucesso!");
        return "redirect:/sessoes/" + sessaoId + "/bugs";
    }
}