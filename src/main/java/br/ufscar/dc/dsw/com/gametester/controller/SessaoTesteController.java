package br.ufscar.dc.dsw.com.gametester.controller;

import br.ufscar.dc.dsw.com.gametester.model.Usuario;
import br.ufscar.dc.dsw.com.gametester.dto.BugCreateDTO;
import br.ufscar.dc.dsw.com.gametester.dto.SessaoCreateDTO;
import br.ufscar.dc.dsw.com.gametester.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/testador/sessoes")
public class SessaoTesteController {

    @Autowired private SessaoTesteService sessaoTesteService;
    @Autowired private BugService bugService;
    @Autowired private ProjetoService projetoService;
    @Autowired private EstrategiaService estrategiaService;

    /**
     * Substitui o TestadorSessaoServlet (doGet)
     * Exibe o formulário para criar uma sessão de teste.
     */
    @GetMapping("/nova")
    public String exibirFormularioCriacao(Model model, @AuthenticationPrincipal Usuario testador) {
        model.addAttribute("sessaoDTO", new SessaoCreateDTO(null, null, 30, ""));
        // Popula os dropdowns do formulário com projetos e estratégias
        model.addAttribute("projetosDoTestador", projetoService.listarProjetosDoUsuario(testador, null));
        model.addAttribute("estrategias", estrategiaService.listarTodas());
        return "testador/sessao-formulario";
    }

    /**
     * Substitui o TestadorSessaoServlet (doPost)
     * Processa a criação da nova sessão de teste.
     */
    @PostMapping
    public String processarCriacao(@Valid @ModelAttribute("sessaoDTO") SessaoCreateDTO dto, BindingResult result,
                                   @AuthenticationPrincipal Usuario testador, RedirectAttributes ra, Model model) {
        if (result.hasErrors()) {
            // Se houver erro de validação, repopula os dropdowns e retorna ao formulário
            model.addAttribute("projetosDoTestador", projetoService.listarProjetosDoUsuario(testador, null));
            model.addAttribute("estrategias", estrategiaService.listarTodas());
            return "testador/sessao-formulario";
        }
        try {
            sessaoTesteService.criarSessao(dto, testador);
            ra.addFlashAttribute("mensagemSucesso", "Sessão de teste criada com sucesso!");
        } catch (Exception e) {
            ra.addFlashAttribute("mensagemErro", "Erro ao criar sessão: " + e.getMessage());
        }
        return "redirect:/testador/minhasSessoes"; // Redireciona para a lista de sessões
    }

    /**
     * Substitui o IniciarSessaoServlet
     */
    @PostMapping("/{id}/iniciar")
    public String iniciarSessao(@PathVariable("id") Long sessaoId, @AuthenticationPrincipal Usuario usuarioLogado, RedirectAttributes ra) {
        try {
            sessaoTesteService.iniciarSessao(sessaoId, usuarioLogado);
            ra.addFlashAttribute("mensagemSucesso", "Sessão (ID: " + sessaoId + ") iniciada!");
            return "redirect:/testador/sessoes/" + sessaoId; // Redireciona para a página da sessão
        } catch (Exception e) {
            ra.addFlashAttribute("mensagemErro", "Erro ao iniciar sessão: " + e.getMessage());
            return "redirect:/testador/minhasSessoes";
        }
    }

    /**
     * Substitui o FinalizarSessaoServlet
     */
    @PostMapping("/{id}/finalizar")
    public String finalizarSessao(@PathVariable("id") Long sessaoId, @AuthenticationPrincipal Usuario usuarioLogado, RedirectAttributes ra) {
        try {
            sessaoTesteService.finalizarSessao(sessaoId, usuarioLogado);
            ra.addFlashAttribute("mensagemSucesso", "Sessão (ID: " + sessaoId + ") finalizada com sucesso!");
        } catch (Exception e) {
            ra.addFlashAttribute("mensagemErro", "Erro ao finalizar sessão: " + e.getMessage());
        }
        return "redirect:/testador/minhasSessoes";
    }

    /**
     * Substitui o VisualizarBugsSessaoServlet e o RegistrarBugServlet (doGet)
     * Exibe a página de detalhes de uma sessão, que inclui a lista de bugs e o formulário para registrar um novo.
     */
    @GetMapping("/{id}")
    public String visualizarSessao(@PathVariable("id") Long sessaoId, Model model, @AuthenticationPrincipal Usuario usuarioLogado) {
        try {
            model.addAttribute("sessao", sessaoTesteService.buscarSessaoParaVisualizacao(sessaoId, usuarioLogado));
            model.addAttribute("bugDTO", new BugCreateDTO("", "", "")); // Prepara o DTO para o form de novo bug
            return "testador/sessao-detalhe";
        } catch (Exception e) {
            // Idealmente, usar RedirectAttributes aqui, mas como é um GET, pode-se usar a sessão ou um parâmetro de erro.
            // Por simplicidade, vamos redirecionar para a lista com um atributo flash (se configurado para funcionar com redirect)
            // RedirectAttributes não são injetáveis em métodos GET por padrão.
            return "redirect:/testador/minhasSessoes?erro=" + e.getMessage();
        }
    }

    /**
     * Substitui o RegistrarBugServlet (doPost)
     * Processa o registro de um novo bug para uma sessão.
     */
    @PostMapping("/{id}/bugs")
    public String registrarBug(
            @PathVariable("id") Long sessaoId, // 1. Pega o ID da sessão (Long) da URL
            @Valid @ModelAttribute("bugDTO") BugCreateDTO dto, // 2. Pega o DTO do formulário
            BindingResult result,
            @AuthenticationPrincipal Usuario usuarioLogado, // 3. Pega o usuário logado
            RedirectAttributes ra,
            Model model) {

        try {
            if (result.hasErrors()) {
                // Se a validação falhar, recarrega a página de detalhes
                model.addAttribute("sessao", sessaoTesteService.buscarSessaoParaVisualizacao(sessaoId, usuarioLogado));
                return "testador/sessao-detalhe";
            }

            // ✅ A CHAMADA CORRETA:
            // Passa o DTO, o ID da sessão e o usuário logado para o serviço.
            bugService.criarBug(dto, sessaoId, usuarioLogado);

            ra.addFlashAttribute("mensagemSucesso", "Novo bug registrado com sucesso!");
        } catch (Exception e) {
            ra.addFlashAttribute("mensagemErro", "Erro ao registrar bug: " + e.getMessage());
        }

        // Redireciona de volta para a página de detalhes da sessão
        return "redirect:/testador/sessoes/" + sessaoId;
    }

}