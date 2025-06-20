package br.ufscar.dc.dsw.com.gametester.config;

import br.ufscar.dc.dsw.com.gametester.domain.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // Pega o objeto do usuário logado
        Usuario usuario = (Usuario) authentication.getPrincipal();

        String redirectURL = request.getContextPath();

        // Lógica de redirecionamento baseada no perfil
        if (usuario.getTipoPerfil().name().equals("ROLE_ADMINISTRADOR")) {
            redirectURL = "/admin/dashboard"; // Ou /admin/dashboard
        } else if (usuario.getTipoPerfil().name().equals("ROLE_TESTADOR")) {
            redirectURL = "/testador/dashboard"; // Ou /testador/dashboard
        }

        response.sendRedirect(redirectURL);
    }
}