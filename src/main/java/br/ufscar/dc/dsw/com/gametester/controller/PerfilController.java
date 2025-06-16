package br.ufscar.dc.dsw.com.gametester.controller;

import br.ufscar.dc.dsw.com.gametester.model.Usuario;
import br.ufscar.dc.dsw.com.gametester.dto.PerfilEditDTO;
import br.ufscar.dc.dsw.com.gametester.dto.SenhaChangeDTO;
import br.ufscar.dc.dsw.com.gametester.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/perfil")
public class PerfilController {

    @Autowired
    private UsuarioService usuarioService;

    // --- MÉTODOS PARA EDITAR PERFIL ---

    /**
     * Substitui o doGet do EditarPerfilServlet.
     * Exibe o formulário de edição de perfil com os dados do usuário logado.
     */
    @GetMapping("/editar")
    public String mostrarFormularioEditar(@AuthenticationPrincipal Usuario usuarioLogado, Model model) {
        // Se não houver um DTO vindo de um erro de POST, cria um novo com os dados atuais
        if (!model.containsAttribute("perfilDTO")) {
            model.addAttribute("perfilDTO", new PerfilEditDTO(usuarioLogado.getNome(), usuarioLogado.getEmail()));
        }
        return "perfil/editar-perfil";
    }

    /**
     * Substitui o doPost do EditarPerfilServlet.
     * Processa a atualização do perfil.
     */
    @PostMapping("/editar")
    public String processarEdicaoPerfil(@Valid @ModelAttribute("perfilDTO") PerfilEditDTO dto, BindingResult result,
                                        @AuthenticationPrincipal Usuario usuarioLogado, RedirectAttributes ra) {
        if (result.hasErrors()) {
            return "perfil/editar-perfil"; // Retorna ao formulário com os erros de validação
        }
        try {
            usuarioService.atualizarPerfil(usuarioLogado, dto);
            ra.addFlashAttribute("mensagemSucesso", "Perfil atualizado com sucesso!");
        } catch (Exception e) {
            ra.addFlashAttribute("mensagemErro", "Erro ao atualizar perfil: " + e.getMessage());
        }
        return "redirect:/perfil/editar";
    }

    // --- MÉTODOS PARA ALTERAR SENHA ---

    /**
     * Substitui o doGet do AlterarSenhaServlet.
     * Exibe o formulário para alterar a senha.
     */
    @GetMapping("/alterar-senha")
    public String mostrarFormularioSenha(Model model) {
        model.addAttribute("senhaDTO", new SenhaChangeDTO("", "", ""));
        return "perfil/alterar-senha";
    }

    /**
     * Substitui o doPost do AlterarSenhaServlet.
     * Processa a alteração da senha.
     */
    @PostMapping("/alterar-senha")
    public String processarAlteracaoSenha(@Valid @ModelAttribute("senhaDTO") SenhaChangeDTO dto, BindingResult result,
                                          @AuthenticationPrincipal Usuario usuarioLogado, RedirectAttributes ra) {
        if (result.hasErrors()) {
            return "perfil/alterar-senha"; // Retorna ao formulário com os erros de validação
        }
        try {
            usuarioService.alterarSenha(usuarioLogado, dto);
            ra.addFlashAttribute("mensagemSucesso", "Senha alterada com sucesso!");
        } catch (Exception e) {
            ra.addFlashAttribute("mensagemErro", e.getMessage());
        }
        return "redirect:/perfil/alterar-senha";
    }
}