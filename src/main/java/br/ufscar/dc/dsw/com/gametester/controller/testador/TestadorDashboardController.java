package br.ufscar.dc.dsw.com.gametester.controller.testador; // ✅ Pacote correto

import br.ufscar.dc.dsw.com.gametester.domain.Projeto;
import br.ufscar.dc.dsw.com.gametester.domain.SessaoTeste;
import br.ufscar.dc.dsw.com.gametester.domain.Usuario;
import br.ufscar.dc.dsw.com.gametester.service.ProjetoService;
import br.ufscar.dc.dsw.com.gametester.service.SessaoTesteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/testador") // ✅ Rota base para este controller
public class TestadorDashboardController {

    @Autowired
    private ProjetoService projetoService;

    @Autowired
    private SessaoTesteService sessaoTesteService;

    /**
     * Exibe a página principal (dashboard) do testador.
     * URL: /testador/dashboard
     */
    @GetMapping("/dashboard") // ✅ Mapeamento para o dashboard
    public String dashboard() {
        return "testador/dashboard"; // Retorna o template dashboard.html
    }

    /**
     * Exibe a página "Meus Projetos" com ordenação.
     * URL: /testador/meus-projetos
     */
    @GetMapping("/meus-projetos")
    public String mostrarMeusProjetos(
            @AuthenticationPrincipal Usuario usuarioLogado,
            @RequestParam(name = "sort", defaultValue = "nome") String sortField,
            @RequestParam(name = "order", defaultValue = "asc") String sortOrder,
            Model model) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder.toUpperCase()), sortField);
        List<Projeto> projetos = projetoService.listarProjetosDoUsuario(usuarioLogado, sort);

        model.addAttribute("listaMeusProjetos", projetos);
        model.addAttribute("currentSortField", sortField);
        model.addAttribute("currentSortOrder", sortOrder);
        model.addAttribute("reverseSortOrder", sortOrder.equals("asc") ? "desc" : "asc");

        return "testador/meus-projetos";
    }

    /**
     * Exibe a página "Minhas Sessões".
     * URL: /testador/sessoes
     */
    /*@GetMapping("/sessoes")
    public String mostrarMinhasSessoes(@AuthenticationPrincipal Usuario usuarioLogado, Model model) {
        List<SessaoTeste> sessoes = sessaoTesteService.listarSessoesPorTestador(usuarioLogado);
        model.addAttribute("listaMinhasSessoes", sessoes);
        return "testador/minhas-sessoes";
    }
*/
    /**
     * Exibe as sessões de um projeto específico.
     * URL: /testador/projetos/{id}/sessoes
     */
    @GetMapping("/projetos/{id}/sessoes")
    public String mostrarSessoesPorProjeto(
            @PathVariable("id") Integer projetoId,
            @AuthenticationPrincipal Usuario usuarioLogado,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            List<SessaoTeste> sessoes = sessaoTesteService.listarSessoesPorProjeto(projetoId, usuarioLogado);
            Projeto projeto = projetoService.buscarPorId(projetoId);

            model.addAttribute("projeto", projeto);
            model.addAttribute("listaSessoesDoProjeto", sessoes);

            return "testador/sessoes-por-projeto";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", e.getMessage());
            return "redirect:/testador/minhas-sessoes";
        }
    }
}