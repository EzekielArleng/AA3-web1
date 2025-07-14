package br.ufscar.dc.dsw.com.gametester.config;

import br.ufscar.dc.dsw.com.gametester.domain.Usuario;
import br.ufscar.dc.dsw.com.gametester.repository.UsuarioRepository;
import br.ufscar.dc.dsw.com.gametester.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var tokenJWT = recuperarToken(request);

        // Se um token foi enviado na requisição...
        if (tokenJWT != null) {
            // 1. Valida o token e extrai o "subject" (que é o e-mail do usuário)
            var subject = tokenService.getSubject(tokenJWT);

            // 2. Com o e-mail, busca o objeto Usuario completo no banco de dados
            var usuario = usuarioRepository.findByEmailIgnoreCase(subject).orElse(null);

            if (usuario != null) {
                // 3. CRIA um objeto de autenticação para esta requisição específica.
                //    A parte mais importante é `usuario.getAuthorities()`, que passa
                //    os perfis (ROLE_ADMINISTRADOR) para o Spring Security.
                var authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());

                // 4. DEFINE o usuário como autenticado no contexto de segurança do Spring.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // Continua a cadeia de filtros, permitindo que a requisição prossiga.
        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {
        var authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null) {
            // O token vem no formato "Bearer eyJhbGciOi..."
            // Esta linha remove o "Bearer " para obter apenas o token.
            return authorizationHeader.replace("Bearer ", "");
        }
        return null;
    }
}