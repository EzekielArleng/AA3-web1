package br.ufscar.dc.dsw.com.gametester.config;

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

@Component // Diz ao Spring para gerenciar esta classe como um componente
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var tokenJWT = recuperarToken(request);

        if (tokenJWT != null) {
            // Valida o token e pega o subject (e-mail)
            var subject = tokenService.getSubject(tokenJWT);
            // Busca o usuário no banco de dados
            var usuario = usuarioRepository.findByEmailIgnoreCase(subject).orElse(null);

            if (usuario != null) {
                // Se o usuário for encontrado, nós o autenticamos para esta requisição
                var authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // Continua a cadeia de filtros
        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {
        var authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null) {
            // Remove o prefixo "Bearer " para obter apenas o token
            return authorizationHeader.replace("Bearer ", "");
        }
        return null;
    }
}