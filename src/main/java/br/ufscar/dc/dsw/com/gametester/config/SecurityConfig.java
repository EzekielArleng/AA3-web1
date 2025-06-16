package br.ufscar.dc.dsw.com.gametester.config; // Ou o pacote de configuração do seu projeto

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // 1. Diz ao Spring que esta é uma classe de configuração
@EnableWebSecurity // 2. Habilita a configuração de segurança web do Spring
public class SecurityConfig {

    /**
     * Este método, anotado com @Bean, age como uma "fábrica".
     * Ele cria uma instância de BCryptPasswordEncoder e a registra no contêiner do Spring
     * sob o tipo "PasswordEncoder".
     *
     * Agora, quando o UsuarioService pedir um PasswordEncoder, o Spring saberá
     * como criar e fornecer um.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     *
     * Em versões modernas do Spring Security, é obrigatório definir um
     * SecurityFilterChain. Sem isso, a aplicação pode não iniciar corretamente.
     * Este é um exemplo básico que libera todos os endpoints.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        // Exemplo: Permitir acesso à página de registro e recursos estáticos
                        .requestMatchers("/usuarios/novo", "/css/**", "/js/**").permitAll()
                        // Exemplo: Exigir autenticação para qualquer outra requisição
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form // Configura o formulário de login
                        .loginPage("/login") // URL da sua página de login customizada
                        .permitAll() // Permite acesso à página de login para todos
                )
                .logout(LogoutConfigurer::permitAll); // Permite que todos façam logout

        return http.build();
    }
}