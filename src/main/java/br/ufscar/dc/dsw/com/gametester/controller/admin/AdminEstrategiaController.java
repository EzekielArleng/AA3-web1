package br.ufscar.dc.dsw.com.gametester.controller.admin;

import br.ufscar.dc.dsw.com.gametester.model.Estrategia;
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
@RequestMapping("/admin/estrategias")
public class AdminEstrategiaController {

    @Autowired
    private EstrategiaService estrategiaService;

    @GetMapping
    public String listarEstrategias(Model model) {
        model.addAttribute("listaEstrategias", estrategiaService.listarTodas());
        return "admin/gerenciar-estrategias";
    }

    @GetMapping("/novo")
    public String mostrarFormularioCadastro(Model model) {
        model.addAttribute("estrategiaDTO", new EstrategiaDTO(null, "", "", "", "", ""));
        return "admin/estrategia-formulario";
    }

    @PostMapping("/novo")
    public String processarCadastro(@Valid @ModelAttribute("estrategiaDTO") EstrategiaDTO dto, BindingResult result, RedirectAttributes ra) {
        if (result.hasErrors()) {
            return "admin/estrategia-formulario";
        }
        try {
            estrategiaService.criarEstrategia(dto);
            ra.addFlashAttribute("mensagemSucesso", "Estratégia cadastrada com sucesso!");
        } catch (Exception e) {
            ra.addFlashAttribute("mensagemErro", "Erro ao cadastrar estratégia: " + e.getMessage());
        }
        return "redirect:/admin/estrategias";
    }

    /**
     * Substitui o doGet do EditarEstrategiaServlet.
     */
    @GetMapping("/{id}/editar")
    public String mostrarFormularioEdicao(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        try {
            Estrategia estrategia = estrategiaService.buscarPorId(id);

            // Mapeia a Entidade para o DTO para preencher o formulário
            EstrategiaDTO dto = new EstrategiaDTO(
                    estrategia.getId(),
                    estrategia.getNome(),
                    estrategia.getDescricao(),
                    estrategia.getExemplos(),
                    estrategia.getDicas(),
                    "" // O campo imagemPath não é relevante na edição, pois já está nos exemplos
            );

            model.addAttribute("estrategiaDTO", dto);

            // Usa o mesmo template do cadastro!
            return "admin/estrategia-formulario";
        } catch (Exception e) {
            ra.addFlashAttribute("mensagemErro", "Estratégia não encontrada.");
            return "redirect:/admin/estrategias";
        }
    }

    /**
     * Substitui o doPost do EditarEstrategiaServlet.
     */
    @PostMapping("/editar")
    public String processarEdicao(@Valid @ModelAttribute("estrategiaDTO") EstrategiaDTO dto,
                                  BindingResult result, RedirectAttributes ra) {
        if (result.hasErrors()) {
            // Se houver erro, retorna para o mesmo formulário.
            // O Thymeleaf irá repopular os campos e exibir os erros.
            return "admin/estrategia-formulario";
        }
        try {
            estrategiaService.editarEstrategia(dto);
            ra.addFlashAttribute("mensagemSucesso", "Estratégia atualizada com sucesso!");
        } catch (Exception e) {
            ra.addFlashAttribute("mensagemErro", "Erro ao editar estratégia: " + e.getMessage());
        }
        return "redirect:/admin/estrategias";
    }

    /**
     * Substitui o ExcluirEstrategiaServlet.
     */
    @PostMapping("/{id}/excluir")
    public String excluirEstrategia(@PathVariable("id") Integer id, RedirectAttributes ra) {
        try {
            estrategiaService.excluirEstrategia(id);
            ra.addFlashAttribute("mensagemSucesso", "Estratégia excluída com sucesso!");
        } catch (Exception e) {
            ra.addFlashAttribute("mensagemErro", "Erro ao excluir estratégia: " + e.getMessage());
        }
        return "redirect:/admin/estrategias";
    }
}