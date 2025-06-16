package br.ufscar.dc.dsw.com.gametester.controller.admin;

import br.ufscar.dc.dsw.com.gametester.model.Usuario;
import br.ufscar.dc.dsw.com.gametester.model.TipoPerfil;
import br.ufscar.dc.dsw.com.gametester.dto.AdminUsuarioCreateDTO;
import br.ufscar.dc.dsw.com.gametester.dto.AdminUsuarioEditDTO;
import br.ufscar.dc.dsw.com.gametester.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/usuarios")
public class AdminUsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String listarUsuarios(Model model) {
        model.addAttribute("listaUsuarios", usuarioService.listarTodos());
        return "admin/gerenciar-usuarios"; // O nome do nosso novo arquivo HTML
    }

    /**
     * Substitui o doGet do CadastrarUsuarioServlet.
     * Exibe o formulário de criação de um novo usuário.
     */
    @GetMapping("/novo")
    public String mostrarFormularioCadastro(Model model) {
        model.addAttribute("usuarioDTO", new AdminUsuarioCreateDTO("", "", "", "", TipoPerfil.ROLE_TESTADOR));
        return "admin/cadastrar-usuario";
    }

    @PostMapping
    public String processarCadastro(@Valid @ModelAttribute("usuarioDTO") AdminUsuarioCreateDTO dto, BindingResult result, RedirectAttributes ra) {
        if (result.hasErrors()) {
            return "admin/cadastrar-usuario";
        }
        try {
            usuarioService.criarUsuarioPorAdmin(dto);
            ra.addFlashAttribute("mensagemSucesso", "Usuário cadastrado com sucesso!");
        } catch (Exception e) {
            ra.addFlashAttribute("mensagemErro", "Erro ao cadastrar usuário: " + e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    /**
     * Substitui o doGet do EditarUsuarioServlet.
     * Exibe o formulário de edição para um usuário específico.
     */
    @GetMapping("/{id}/editar")
    public String mostrarFormularioEdicao(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
        try {
            Usuario usuario = usuarioService.buscarPorId(id);
            // Mapeia a Entidade para o DTO para preencher o formulário
            AdminUsuarioEditDTO dto = new AdminUsuarioEditDTO(usuario.getId(), usuario.getNome(), usuario.getEmail(), "", "", usuario.getTipoPerfil());
            model.addAttribute("usuarioDTO", dto);
            return "admin/editar-usuario";
        } catch (Exception e) {
            ra.addFlashAttribute("mensagemErro", "Usuário não encontrado.");
            return "redirect:/admin/usuarios";
        }
    }

    /**
     * Substitui o doPost do EditarUsuarioServlet.
     * Processa a atualização do usuário.
     */
    @PostMapping("/editar")
    public String processarEdicao(@Valid @ModelAttribute("usuarioDTO") AdminUsuarioEditDTO dto, BindingResult result, RedirectAttributes ra) {
        if (result.hasErrors()) {
            return "admin/editar-usuario";
        }
        try {
            usuarioService.editarUsuarioPorAdmin(dto);
            ra.addFlashAttribute("mensagemSucesso", "Usuário atualizado com sucesso!");
        } catch (Exception e) {
            ra.addFlashAttribute("mensagemErro", "Erro ao editar usuário: " + e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    /**
     * Substitui o ExcluirUsuarioServlet.
     * Processa a exclusão de um usuário.
     */
    @PostMapping("/{id}/excluir")
    public String excluirUsuario(@PathVariable("id") Long id, @AuthenticationPrincipal Usuario adminLogado, RedirectAttributes ra) {
        try {
            usuarioService.excluirUsuario(id, adminLogado);
            ra.addFlashAttribute("mensagemSucesso", "Usuário excluído com sucesso!");
        } catch (Exception e) {
            ra.addFlashAttribute("mensagemErro", "Erro ao excluir usuário: " + e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }
}