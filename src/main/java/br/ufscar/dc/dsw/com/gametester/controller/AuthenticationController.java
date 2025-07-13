package br.ufscar.dc.dsw.com.gametester.controller;

import br.ufscar.dc.dsw.com.gametester.domain.Usuario;
import br.ufscar.dc.dsw.com.gametester.dto.LoginRequestDTO;
import br.ufscar.dc.dsw.com.gametester.dto.LoginResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import br.ufscar.dc.dsw.com.gametester.service.TokenService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class AuthenticationController {

    // Injeta o AuthenticationManager do Spring Security e um serviço de Token
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO dados) {
        // Valida as credenciais
        var authenticationToken = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());
        var authentication = authenticationManager.authenticate(authenticationToken);

        // Gera o token JWT
        var tokenJWT = tokenService.gerarToken((Usuario) authentication.getPrincipal());

        // Retorna o token para o cliente
        return ResponseEntity.ok(new LoginResponseDTO(tokenJWT));
    }
}