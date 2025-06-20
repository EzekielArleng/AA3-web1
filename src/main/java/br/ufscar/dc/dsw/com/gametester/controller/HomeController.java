package br.ufscar.dc.dsw.com.gametester.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.time.Year;

@Controller
public class HomeController {

    @GetMapping("/index") // Mapeia para a raiz do site
    public String index(Model model) {
        // Adiciona o ano atual ao modelo para ser usado no template
        model.addAttribute("currentYear", Year.now().getValue());
        return "index"; // Retorna o nome do arquivo de template: /templates/index.html
    }
}