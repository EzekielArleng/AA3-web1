package br.ufscar.dc.dsw.com.gametester.controller;

import br.ufscar.dc.dsw.com.gametester.domain.Usuario;
import br.ufscar.dc.dsw.com.gametester.dto.LoginRequestDTO;
import br.ufscar.dc.dsw.com.gametester.dto.LoginResponseDTO;
import br.ufscar.dc.dsw.com.gametester.repository.UsuarioRepository; // ✅ Import
import br.ufscar.dc.dsw.com.gametester.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder; // ✅ Import
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthenticationController {

    // Vamos injetar o que precisamos para o debug
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TokenService tokenService;

    // MÉTODO DE LOGIN TEMPORÁRIO PARA DEBUG
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequestDTO dados) {
        System.out.println("--- INICIANDO DEBUG DO LOGIN ---");
        System.out.println("Tentando autenticar: " + dados.email());

        // Passo 1: Buscar o usuário no banco
        var usuarioOptional = usuarioRepository.findByEmailIgnoreCase(dados.email());
        if (usuarioOptional.isEmpty()) {
            System.out.println("ERRO DEBUG: Usuário não encontrado no banco.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas (usuário não encontrado).");
        }

        Usuario usuario = usuarioOptional.get();
        System.out.println("Usuário encontrado: " + usuario.getUsername());

        // Passo 2: Verificar a senha
        boolean senhasBatem = passwordEncoder.matches(dados.senha(), usuario.getPassword());
        if (!senhasBatem) {
            System.out.println("ERRO DEBUG: As senhas não batem!");
            System.out.println("Senha enviada: " + dados.senha());
            System.out.println("Senha no banco (hash): " + usuario.getPassword());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas (senha incorreta).");
        }
        System.out.println("OK DEBUG: As senhas batem.");

        // Passo 3: Verificar o status da conta (a causa mais provável do 403)
        System.out.println("Verificando status da conta...");
        System.out.println("isEnabled(): " + usuario.isEnabled());
        System.out.println("isAccountNonLocked(): " + usuario.isAccountNonLocked());
        // Adicione outras verificações se necessário

        if (!usuario.isEnabled()) {
            System.out.println("ERRO DEBUG: Conta está desabilitada (isEnabled() retornou false).");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Conta desabilitada.");
        }
        System.out.println("OK DEBUG: Conta está habilitada.");

        // Se tudo passou, geramos o token
        var tokenJWT = tokenService.gerarToken(usuario);
        System.out.println("--- FIM DO DEBUG ---");
        return ResponseEntity.ok(new LoginResponseDTO(tokenJWT));
    }
}