package br.ufscar.dc.dsw.com.gametester.controller;

import br.ufscar.dc.dsw.com.gametester.domain.Estrategia;
import br.ufscar.dc.dsw.com.gametester.dto.EstrategiaResponseDTO;
import br.ufscar.dc.dsw.com.gametester.service.EstrategiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

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
    public ResponseEntity<List<EstrategiaResponseDTO>> listarTodas(){
        List<Estrategia> estrategias = estrategiaService.listarTodas();
        List<EstrategiaResponseDTO> dtos = estrategias.stream().map(EstrategiaResponseDTO::new).toList();
        return ResponseEntity.ok(dtos);
    }


    /**
     * Exibe a página de detalhes para uma única estratégia.
     * Corresponde ao antigo detalhes-estrategia.jsp
     */
    @GetMapping("/{id}")
    public ResponseEntity<EstrategiaResponseDTO> buscarPorId(@PathVariable("id") Integer id) {
        Estrategia estrategia = estrategiaService.buscarPorId(id);
        return ResponseEntity.ok(new EstrategiaResponseDTO(estrategia));
    }
}