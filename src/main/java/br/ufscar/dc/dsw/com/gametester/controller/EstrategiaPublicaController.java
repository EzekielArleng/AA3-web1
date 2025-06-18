package br.ufscar.dc.dsw.com.gametester.controller;

import br.ufscar.dc.dsw.com.gametester.domain.Estrategia;
import br.ufscar.dc.dsw.com.gametester.service.EstrategiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/estrategias") // Rota base para esta funcionalidade
public class EstrategiaPublicaController {

    @Autowired
    private EstrategiaService estrategiaService;

    /**
     * Exibe a lista pública de todas as estratégias.
     * Corresponde ao antigo listar-estrategias.jsp
     */
    @GetMapping
    public String listarTodas(Model model) {
        model.addAttribute("listaEstrategias", estrategiaService.listarTodas());
        return "estrategias/listar-estrategias";
    }

    /**
     * Exibe a página de detalhes para uma única estratégia.
     * Corresponde ao antigo detalhes-estrategia.jsp
     */
    @GetMapping("/{id}")
    public String verDetalhes(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        try {
            Estrategia estrategia = estrategiaService.buscarPorId(id);
            model.addAttribute("estrategiaDetalhes", estrategia);
            return "estrategias/detalhes-estrategia";
        } catch (Exception e) {
            ra.addFlashAttribute("mensagemErro", "Estratégia não encontrada com o ID: " + id);
            // Se não encontrar, redireciona para a lista geral
            return "redirect:/estrategias";
        }
    }
}