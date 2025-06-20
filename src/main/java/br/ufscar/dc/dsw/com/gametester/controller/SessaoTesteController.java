package br.ufscar.dc.dsw.com.gametester.controller;

import br.ufscar.dc.dsw.com.gametester.domain.SessaoTeste;
import br.ufscar.dc.dsw.com.gametester.domain.Usuario;
import br.ufscar.dc.dsw.com.gametester.dto.BugCreateDTO;
import br.ufscar.dc.dsw.com.gametester.dto.SessaoCreateDTO;
import br.ufscar.dc.dsw.com.gametester.service.BugService;
import br.ufscar.dc.dsw.com.gametester.service.EstrategiaService;
import br.ufscar.dc.dsw.com.gametester.service.ProjetoService;
import br.ufscar.dc.dsw.com.gametester.service.SessaoTesteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/testador/sessoes")
public class SessaoTesteController {

    @Autowired private SessaoTesteService sessaoTesteService;
    @Autowired private BugService bugService;
    @Autowired private ProjetoService projetoService;
    @Autowired private EstrategiaService estrategiaService;

    /**
     * Exibe a lista de todas as sessões do testador logado.
     * Mapeado para a URL base: GET /testador/sessoes
     */
    @GetMapping
    public String listarMinhasSessoes(@AuthenticationPrincipal Usuario testador, Model model) {
        List<SessaoTeste> sessoes = sessaoTesteService.listarSessoesPorTestador(testador);
        model.addAttribute("listaMinhasSessoes", sessoes);
        return "testador/minhas-sessoes";
    }

    /**
     * Exibe o formulário para criar uma nova sessão de teste.
     * Mapeado para GET /testador/sessoes/nova
     */
    @GetMapping("/nova")
    public String exibirFormularioCriacao(Model model, @AuthenticationPrincipal Usuario testador) {
        model.addAttribute("projetosDoTestador", projetoService.listarProjetosDoUsuario(testador, Sort.by("nome")));
        model.addAttribute("estrategias", estrategiaService.listarTodas());
        model.addAttribute("sessaoDTO", new SessaoCreateDTO(null, null, 30, ""));
        return "testador/sessao/formularioSessao";
    }

    /**
     * Processa a criação da nova sessão de teste.
     * Mapeado para POST /testador/sessoes/nova
     */
    @PostMapping("/nova")
    public String processarCriacao(@Valid @ModelAttribute("sessaoDTO") SessaoCreateDTO dto,
                                   BindingResult result, @AuthenticationPrincipal Usuario testador,
                                   RedirectAttributes ra, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("projetosDoTestador", projetoService.listarProjetosDoUsuario(testador, Sort.by("nome")));
            model.addAttribute("estrategias", estrategiaService.listarTodas());
            return "testador/sessao/formularioSessao";
        }
        try {
            sessaoTesteService.criarSessao(dto, testador);
            ra.addFlashAttribute("mensagemSucesso", "Sessão de teste criada com sucesso!");
        } catch (Exception e) {
            ra.addFlashAttribute("mensagemErro", "Erro ao criar sessão: " + e.getMessage());
        }
        return "redirect:/testador/sessoes";
    }

    /**
     * Inicia uma sessão de teste.
     * Mapeado para POST /testador/sessoes/{id}/iniciar
     */
    @PostMapping("/{id}/iniciar")
    public String iniciarSessao(@PathVariable("id") Long sessaoId, @AuthenticationPrincipal Usuario usuarioLogado, RedirectAttributes ra) {
        try {
            sessaoTesteService.iniciarSessao(sessaoId, usuarioLogado);
            ra.addFlashAttribute("mensagemSucesso", "Sessão (ID: " + sessaoId + ") iniciada!");
            return "redirect:/testador/sessoes/" + sessaoId;
        } catch (Exception e) {
            ra.addFlashAttribute("mensagemErro", "Erro ao iniciar sessão: " + e.getMessage());
            return "redirect:/testador/sessoes";
        }
    }

    /**
     * Finaliza uma sessão de teste.
     * Mapeado para POST /testador/sessoes/{id}/finalizar
     */
    @PostMapping("/{id}/finalizar")
    public String finalizarSessao(@PathVariable("id") Long sessaoId, @AuthenticationPrincipal Usuario usuarioLogado, RedirectAttributes ra) {
        try {
            sessaoTesteService.finalizarSessao(sessaoId, usuarioLogado);
            ra.addFlashAttribute("mensagemSucesso", "Sessão (ID: " + sessaoId + ") finalizada com sucesso!");
        } catch (Exception e) {
            ra.addFlashAttribute("mensagemErro", "Erro ao finalizar sessão: " + e.getMessage());
        }
        return "redirect:/testador/sessoes";
    }

    /**
     * Exibe a página de detalhes de uma sessão.
     * Mapeado para GET /testador/sessoes/{id}
     */
    @GetMapping("/{id}")
    public String visualizarSessao(@PathVariable("id") Long sessaoId, Model model, @AuthenticationPrincipal Usuario usuarioLogado, RedirectAttributes ra) {
        try {
            model.addAttribute("sessao", sessaoTesteService.buscarSessaoParaVisualizacao(sessaoId, usuarioLogado));
            model.addAttribute("bugDTO", new BugCreateDTO("", "", ""));
            return "testador/sessao/sessao-detalhe";
        } catch (Exception e) {
            ra.addFlashAttribute("mensagemErro", e.getMessage());
            return "redirect:/testador/sessoes";
        }
    }

    /**
     * Processa o registro de um novo bug para uma sessão.
     * Mapeado para POST /testador/sessoes/{id}/bugs
     */
    @PostMapping("/{id}/bugs")
    public String registrarBug(@PathVariable("id") Long sessaoId, @Valid @ModelAttribute("bugDTO") BugCreateDTO dto,
                               BindingResult result, @AuthenticationPrincipal Usuario usuarioLogado,
                               RedirectAttributes ra, Model model) {
        try {
            if (result.hasErrors()) {
                model.addAttribute("sessao", sessaoTesteService.buscarSessaoParaVisualizacao(sessaoId, usuarioLogado));
                return "testador/sessao/sessao-detalhe";
            }
            bugService.criarBug(dto, sessaoId, usuarioLogado);
            ra.addFlashAttribute("mensagemSucesso", "Novo bug registrado com sucesso!");
        } catch (Exception e) {
            ra.addFlashAttribute("mensagemErro", "Erro ao registrar bug: " + e.getMessage());
        }
        return "redirect:/testador/sessoes/" + sessaoId;
    }
}