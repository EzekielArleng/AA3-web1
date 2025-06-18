package br.ufscar.dc.dsw.com.gametester.controller.admin;

import br.ufscar.dc.dsw.com.gametester.domain.Projeto;
import br.ufscar.dc.dsw.com.gametester.dto.ProjetoDTO;
import br.ufscar.dc.dsw.com.gametester.service.ProjetoService;
import br.ufscar.dc.dsw.com.gametester.service.SessaoTesteService;
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

    @Autowired
    private SessaoTesteService sessaoTesteService;
    /**
     * Substitui o VisualizarSessoesAdminServlet.
     * Exibe a lista de todas as sessões de teste no sistema.
     */
    @GetMapping
    public String listarTodasSessoes(Model model) {
        model.addAttribute("listaSessoes", sessaoTesteService.listarTodasAsSessoes());
        return "admin/visualizar-sessoes"; // Aponta para o nosso novo template
    }

}