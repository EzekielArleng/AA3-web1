package br.ufscar.dc.dsw.com.gametester.controller.admin;

import br.ufscar.dc.dsw.com.gametester.domain.Projeto;
import br.ufscar.dc.dsw.com.gametester.dto.ProjetoDTO;
import br.ufscar.dc.dsw.com.gametester.service.ProjetoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/projetos")
public class AdminProjetoController {

    @Autowired
    private ProjetoService projetoService;

    /**
     * Substitui o GerenciarProjetosServlet.
     * Exibe a lista de todos os projetos com ordenação.
     */
    @GetMapping
    public String listarProjetos(@RequestParam(name = "sort", defaultValue = "nome") String sortField,
                                 @RequestParam(name = "order", defaultValue = "asc") String sortOrder,
                                 Model model) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder.toUpperCase()), sortField);
        model.addAttribute("listaProjetos", projetoService.listarTodos(sort));
        model.addAttribute("currentSortField", sortField);
        model.addAttribute("currentSortOrder", sortOrder);
        model.addAttribute("reverseSortOrder", sortOrder.equals("asc") ? "desc" : "asc");
        return "admin/gerenciar-projetos";
    }

    /**
     * Substitui o doGet do CadastrarProjetoServlet.
     */
    @GetMapping("/novo")
    public String mostrarFormularioCadastro(Model model) {
        model.addAttribute("projetoDTO", new ProjetoDTO(null, "", ""));
        return "admin/projeto-formulario";
    }

    /**
     * Substitui o doPost do CadastrarProjetoServlet.
     */
    @PostMapping("/novo")
    public String processarCadastro(@Valid @ModelAttribute("projetoDTO") ProjetoDTO dto, BindingResult result, RedirectAttributes ra) {
        if (result.hasErrors()) {
            return "admin/projeto-formulario";
        }
        try {
            projetoService.criarProjeto(dto);
            ra.addFlashAttribute("mensagemSucesso", "Projeto cadastrado com sucesso!");
        } catch (Exception e) {
            ra.addFlashAttribute("mensagemErro", "Erro ao cadastrar projeto: " + e.getMessage());
        }
        return "redirect:/admin/projetos";
    }

    /**
     * Substitui o doGet do EditarProjetoServlet.
     */
    @GetMapping("/{id}/editar")
    public String mostrarFormularioEdicao(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        try {
            Projeto projeto = projetoService.buscarPorId(id);
            model.addAttribute("projetoDTO", new ProjetoDTO(projeto.getId(), projeto.getNome(), projeto.getDescricao()));
            return "admin/projeto-formulario"; // Reutiliza a mesma view
        } catch (Exception e) {
            ra.addFlashAttribute("mensagemErro", "Projeto não encontrado.");
            return "redirect:/admin/projetos";
        }
    }

    /**
     * Substitui o doPost do EditarProjetoServlet.
     */
    @PostMapping("/editar")
    public String processarEdicao(@Valid @ModelAttribute("projetoDTO") ProjetoDTO dto, BindingResult result, RedirectAttributes ra) {
        if (result.hasErrors()) {
            // Se houver erro, o ID já está no DTO, então a view saberá que é uma edição
            return "admin/projeto-formulario";
        }
        try {
            projetoService.editarProjeto(dto);
            ra.addFlashAttribute("mensagemSucesso", "Projeto atualizado com sucesso!");
        } catch (Exception e) {
            ra.addFlashAttribute("mensagemErro", "Erro ao editar projeto: " + e.getMessage());
        }
        return "redirect:/admin/projetos";
    }

    /**
     * Substitui o ExcluirProjetoServlet.
     */
    @PostMapping("/{id}/excluir")
    public String excluirProjeto(@PathVariable("id") Integer id, RedirectAttributes ra) {
        try {
            projetoService.excluir(id);
            ra.addFlashAttribute("mensagemSucesso", "Projeto excluído com sucesso!");
        } catch (Exception e) {
            ra.addFlashAttribute("mensagemErro", "Erro ao excluir projeto: " + e.getMessage());
        }
        return "redirect:/admin/projetos";
    }

    /**
     * Substitui o GerenciarMembrosProjetoServlet (doGet).
     */
    @GetMapping("/{id}/membros")
    public String gerenciarMembros(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        try {
            model.addAttribute("projeto", projetoService.buscarPorId(id));
            model.addAttribute("membrosAtuais", projetoService.listarMembrosDoProjeto(id));
            model.addAttribute("usuariosDisponiveis", projetoService.listarUsuariosDisponiveisParaProjeto(id));
            return "admin/gerenciar-membros-projeto";
        } catch (Exception e) {
            ra.addFlashAttribute("mensagemErro", "Erro ao carregar projeto: " + e.getMessage());
            return "redirect:/admin/projetos";
        }
    }

    /**
     * Substitui parte do doPost do GerenciarMembrosProjetoServlet (adicionar).
     */
    @PostMapping("/{id}/membros/adicionar")
    public String adicionarMembro(@PathVariable("id") Integer projetoId, @RequestParam("usuarioId") Long usuarioId, RedirectAttributes ra) {
        try {
            projetoService.adicionarMembro(projetoId, usuarioId);
            ra.addFlashAttribute("mensagemSucesso", "Membro adicionado com sucesso!");
        } catch(Exception e) {
            ra.addFlashAttribute("mensagemErro", "Erro ao adicionar membro: " + e.getMessage());
        }
        return "redirect:/admin/projetos/" + projetoId + "/membros";
    }

    /**
     * Substitui parte do doPost do GerenciarMembrosProjetoServlet (remover).
     */
    @PostMapping("/{projetoId}/membros/{usuarioId}/remover")
    public String removerMembro(@PathVariable Integer projetoId, @PathVariable Long usuarioId, RedirectAttributes ra) {
        try {
            projetoService.removerMembro(projetoId, usuarioId);
            ra.addFlashAttribute("mensagemSucesso", "Membro removido com sucesso!");
        } catch(Exception e) {
            ra.addFlashAttribute("mensagemErro", "Erro ao remover membro: " + e.getMessage());
        }
        return "redirect:/admin/projetos/" + projetoId + "/membros";
    }
}