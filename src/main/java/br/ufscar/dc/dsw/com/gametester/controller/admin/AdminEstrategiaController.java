package br.ufscar.dc.dsw.com.gametester.controller.admin;

import br.ufscar.dc.dsw.com.gametester.domain.Estrategia;
import br.ufscar.dc.dsw.com.gametester.dto.EstrategiaDTO;
import br.ufscar.dc.dsw.com.gametester.service.EstrategiaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/estrategias") // Mapeado para a mesma URL do servlet
public class AdminEstrategiaController {

    @Autowired
    private EstrategiaService estrategiaService;

    /**
     * Substitui o método doGet do servlet, tratando as ações via parâmetro.
     */
    @GetMapping
    public String doGetActions(@RequestParam(name = "action", defaultValue = "listar") String action,
                               @RequestParam(name = "id", required = false) Integer id,
                               Model model, RedirectAttributes ra) {
        try {
            switch (action) {
                case "novo":
                    return mostrarFormularioNovaEstrategia(model);
                case "editar":
                    return mostrarFormularioEditarEstrategia(id, model);
                case "excluir":
                    return excluirEstrategia(id, ra);
                case "listar":
                default:
                    return listarEstrategias(model);
            }
        } catch (Exception e) {
            ra.addFlashAttribute("mensagemErro", "Erro ao processar a ação: " + e.getMessage());
            return "redirect:/admin/estrategias";
        }
    }

    /**
     * Substitui o método doPost do servlet.
     */
    @PostMapping
    public String doPostActions(@RequestParam(name = "action", defaultValue = "") String action,
                                @Valid @ModelAttribute("estrategiaDTO") EstrategiaDTO dto,
                                BindingResult result, RedirectAttributes ra, Model model) {
        if ("salvar".equals(action)) {
            return salvarEstrategia(dto, result, ra, model);
        }
        // Redireciona para a lista se a ação POST for desconhecida
        return "redirect:/admin/estrategias";
    }

    // --- MÉTODOS PRIVADOS (Lógica interna, similar ao servlet) ---

    private String listarEstrategias(Model model) {
        model.addAttribute("listaEstrategias", estrategiaService.listarTodas());
        return "admin/estrategia/lista"; // Aponta para lista.html
    }

    private String mostrarFormularioNovaEstrategia(Model model) {
        model.addAttribute("estrategiaDTO", new EstrategiaDTO(null, "", "", "", "", ""));
        model.addAttribute("action", "salvar");
        return "admin/estrategia/formulario"; // Aponta para formulario.html
    }

    private String mostrarFormularioEditarEstrategia(Integer id, Model model) {
        Estrategia estrategia = estrategiaService.buscarPorId(id);
        EstrategiaDTO dto = new EstrategiaDTO(estrategia.getId(), estrategia.getNome(), estrategia.getDescricao(), estrategia.getExemplos(), estrategia.getDicas(), "");
        model.addAttribute("estrategiaDTO", dto);
        model.addAttribute("action", "salvar");
        return "admin/estrategia/formulario";
    }

    private String salvarEstrategia(EstrategiaDTO dto, BindingResult result, RedirectAttributes ra, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("action", "salvar");
            return "admin/estrategia/formulario";
        }
        try {
            if (dto.id() == null) { // Criação
                estrategiaService.criarEstrategia(dto);
                ra.addFlashAttribute("mensagemSucesso", "Estratégia cadastrada com sucesso!");
            } else { // Edição
                estrategiaService.editarEstrategia(dto);
                ra.addFlashAttribute("mensagemSucesso", "Estratégia atualizada com sucesso!");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("mensagemErro", "Erro ao salvar estratégia: " + e.getMessage());
        }
        return "redirect:/admin/estrategias";
    }

    private String excluirEstrategia(Integer id, RedirectAttributes ra) {
        try {
            estrategiaService.excluirEstrategia(id);
            ra.addFlashAttribute("mensagemSucesso", "Estratégia excluída com sucesso!");
        } catch (Exception e) {
            ra.addFlashAttribute("mensagemErro", "Erro ao excluir estratégia: " + e.getMessage());
        }
        return "redirect:/admin/estrategias";
    }
}