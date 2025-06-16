package br.ufscar.dc.dsw.com.gametester.controller;

import br.ufscar.dc.dsw.com.gametester.model.Projeto;
import br.ufscar.dc.dsw.com.gametester.model.SessaoTeste;
import br.ufscar.dc.dsw.com.gametester.model.Usuario;
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
@RequestMapping("/testador") // Rota base para todas as URLs deste controller
public class TestadorDashboardController {

    @Autowired
    private ProjetoService projetoService;

    @Autowired
    private SessaoTesteService sessaoTesteService;

    /**
     * Substitui o MeusProjetosServlet.
     * Lida com a ordenação dinâmica de forma elegante usando os parâmetros da requisição.
     */
    @GetMapping("/meus-projetos")
    public String mostrarMeusProjetos(
            @AuthenticationPrincipal Usuario usuarioLogado,
            @RequestParam(name = "sort", defaultValue = "nome") String sortField, // Pega o campo de ordenação
            @RequestParam(name = "order", defaultValue = "asc") String sortOrder, // Pega a direção
            Model model) {

        // Cria o objeto Sort do Spring a partir dos parâmetros da URL
        Sort.Direction direction = Sort.Direction.fromString(sortOrder.toUpperCase());
        Sort sort = Sort.by(direction, sortField);

        List<Projeto> projetos = projetoService.listarProjetosDoUsuario(usuarioLogado, sort);

        model.addAttribute("listaMeusProjetos", projetos);
        model.addAttribute("currentSortField", sortField); // Para a view saber qual coluna está ativa
        model.addAttribute("currentSortOrder", sortOrder);
        // Usado para inverter a ordem na próxima vez que o link for clicado
        model.addAttribute("reverseSortOrder", sortOrder.equals("asc") ? "desc" : "asc");

        return "testador/meus-projetos"; // Caminho para o arquivo Thymeleaf/JSP
    }

    /**
     * Substitui o MinhasSessoesServlet.
     * Simplesmente lista as sessões do testador logado.
     */
    @GetMapping("/sessoes")
    public String mostrarMinhasSessoes(@AuthenticationPrincipal Usuario usuarioLogado, Model model) {
        List<SessaoTeste> sessoes = sessaoTesteService.listarSessoesPorTestador(usuarioLogado);
        model.addAttribute("listaMinhasSessoes", sessoes);
        return "testador/minhas-sessoes";
    }

    /**
     * Substitui o SessoesPorProjetoServlet.
     * Usa uma URL mais limpa e RESTful.
     */
    @GetMapping("/projetos/{id}/sessoes")
    public String mostrarSessoesPorProjeto(
            @PathVariable("id") Integer projetoId,
            @AuthenticationPrincipal Usuario usuarioLogado,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            // A lógica de permissão está dentro do serviço, mantendo o controller limpo.
            List<SessaoTeste> sessoes = sessaoTesteService.listarSessoesPorProjeto(projetoId, usuarioLogado);
            Projeto projeto = projetoService.buscarPorId(projetoId); // Busca o projeto para exibir seu nome/descrição

            model.addAttribute("projeto", projeto);
            model.addAttribute("listaSessoesDoProjeto", sessoes);

            return "testador/sessoes-por-projeto";
        } catch (Exception e) {
            // Se o serviço lançar uma exceção (ex: permissão negada), captura aqui.
            redirectAttributes.addFlashAttribute("mensagemErro", e.getMessage());
            return "redirect:/testador/meus-projetos";
        }
    }
}