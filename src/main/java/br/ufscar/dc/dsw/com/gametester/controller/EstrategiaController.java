// src/main/java/com/gametester/controller/EstrategiaController.java
package br.ufscar.dc.dsw.com.gametester.controller;

import br.ufscar.dc.dsw.com.gametester.domain.Estrategia;
import br.ufscar.dc.dsw.com.gametester.service.EstrategiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/estrategias") // Todas as URLs deste controller começarão com /estrategias
public class EstrategiaController {

    private final EstrategiaService estrategiaService;

    @Autowired
    public EstrategiaController(EstrategiaService estrategiaService) {
        this.estrategiaService = estrategiaService;
    }

    // LISTAR todas as estratégias
    @GetMapping
    public String listarEstrategias(Model model) {
        model.addAttribute("listaEstrategias", estrategiaService.listarTodas());
        return "estrategia/lista"; // Renderiza o arquivo /templates/estrategia/lista.html
    }

    // MOSTRAR formulário de nova estratégia
    @GetMapping("/nova")
    public String mostrarFormulario(Model model) {
        model.addAttribute("estrategia", new Estrategia());
        model.addAttribute("tituloPagina", "Nova Estratégia");
        return "estrategia/formulario"; // Renderiza /templates/estrategia/formulario.html
    }

    // SALVAR (criar ou atualizar) uma estratégia
    @PostMapping("/salvar")
    public String salvarEstrategia(@ModelAttribute("estrategia") Estrategia estrategia, RedirectAttributes ra) {
        try {
            estrategiaService.salvar(estrategia);
            ra.addFlashAttribute("mensagemSucesso", "Estratégia salva com sucesso!");
        } catch (Exception e) {
            ra.addFlashAttribute("mensagemErro", "Erro ao salvar estratégia: " + e.getMessage());
            // Opcional: redirecionar de volta para o formulário com os dados preenchidos
            // return "estrategia/formulario";
        }
        return "redirect:/estrategias";
    }

    // EDITAR uma estratégia
    @GetMapping("/{id}/editar")
    public String mostrarFormularioEdicao(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        try {
            Estrategia estrategia = estrategiaService.buscarPorId(id);
            model.addAttribute("estrategia", estrategia);
            model.addAttribute("tituloPagina", "Editar Estratégia");
            return "estrategia/formulario";
        } catch (RuntimeException e) {
            ra.addFlashAttribute("mensagemErro", e.getMessage());
            return "redirect:/estrategias";
        }
    }

    // EXCLUIR uma estratégia
    @GetMapping("/{id}/excluir")
    public String excluirEstrategia(@PathVariable("id") Integer id, RedirectAttributes ra) {
        try {
            estrategiaService.deletar(id);
            ra.addFlashAttribute("mensagemSucesso", "Estratégia com ID " + id + " excluída com sucesso.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("mensagemErro", e.getMessage());
        }
        return "redirect:/estrategias";
    }
}