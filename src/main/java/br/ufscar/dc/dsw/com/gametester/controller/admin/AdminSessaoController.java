package br.ufscar.dc.dsw.com.gametester.controller.admin;

import br.ufscar.dc.dsw.com.gametester.domain.SessaoTeste;
import br.ufscar.dc.dsw.com.gametester.dto.AdminSessaoEditDTO;
import br.ufscar.dc.dsw.com.gametester.service.EstrategiaService;
import br.ufscar.dc.dsw.com.gametester.service.ProjetoService;
import br.ufscar.dc.dsw.com.gametester.service.SessaoTesteService;
import br.ufscar.dc.dsw.com.gametester.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String listarTodasSessoes(Model model) {
        model.addAttribute("listaSessoes", sessaoTesteService.listarTodasAsSessoes());
        return "admin/visualizar-sessoes";
    }

    @GetMapping("/{id}/editar")
    public String mostrarFormularioEdicao(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
        try {
            SessaoTeste sessao = sessaoTesteService.buscarPorId(id);

            // Mapeia a Entidade para o DTO para preencher o formulário
            AdminSessaoEditDTO dto = new AdminSessaoEditDTO(
                    sessao.getId(),
                    sessao.getProjeto().getId(),
                    sessao.getTestador().getId(),
                    sessao.getEstrategia().getId(),
                    sessao.getTempoSessaoMinutos(),
                    sessao.getDescricao(),
                    sessao.getStatus(),
                    sessao.getDataHoraInicio(),
                    sessao.getDataHoraFim()
            );

            model.addAttribute("sessaoDTO", dto);

            // Adiciona as listas para preencher os dropdowns do formulário
            model.addAttribute("todosProjetos", projetoService.listarTodos(Sort.by("nome")));
            model.addAttribute("todosTestadores", usuarioService.listarTodos());
            model.addAttribute("todasEstrategias", estrategiaService.listarTodas());

            return "admin/editar-sessao-admin";
        } catch (Exception e) {
            ra.addFlashAttribute("mensagemErro", "Sessão não encontrada: " + e.getMessage());
            return "redirect:/admin/sessoes";
        }
    }

    /**
     * ✅ MÉTODO FALTANTE: Processa a atualização da sessão.
     */
    @PostMapping("/editar")
    public String processarEdicao(@Valid @ModelAttribute("sessaoDTO") AdminSessaoEditDTO dto,
                                  BindingResult result, RedirectAttributes ra, Model model) {
        if (result.hasErrors()) {
            // Se houver erro de validação, repopula os dropdowns antes de retornar à view
            model.addAttribute("todosProjetos", projetoService.listarTodos(null));
            model.addAttribute("todosTestadores", usuarioService.listarTodos());
            model.addAttribute("todasEstrategias", estrategiaService.listarTodas());
            return "admin/editar-sessao-admin";
        }
        try {
            sessaoTesteService.editarSessaoPorAdmin(dto);
            ra.addFlashAttribute("mensagemSucesso", "Sessão atualizada com sucesso!");
        } catch (Exception e) {
            ra.addFlashAttribute("mensagemErro", "Erro ao editar sessão: " + e.getMessage());
        }
        return "redirect:/admin/sessoes";
    }

    /**
     * ✅ MÉTODO FALTANTE: Processa a exclusão da sessão.
     */
    @PostMapping("/{id}/excluir")
    public String excluirSessao(@PathVariable("id") Long id, RedirectAttributes ra) {
        try {
            sessaoTesteService.excluirSessao(id);
            ra.addFlashAttribute("mensagemSucesso", "Sessão excluída com sucesso!");
        } catch (Exception e) {
            ra.addFlashAttribute("mensagemErro", "Erro ao excluir sessão: " + e.getMessage());
        }
        return "redirect:/admin/sessoes";
    }
}