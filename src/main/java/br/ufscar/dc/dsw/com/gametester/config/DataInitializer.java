package br.ufscar.dc.dsw.com.gametester.config;

import br.ufscar.dc.dsw.com.gametester.domain.Usuario;
import br.ufscar.dc.dsw.com.gametester.domain.enums.TipoPerfil;
import br.ufscar.dc.dsw.com.gametester.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (usuarioRepository.count() == 0) {
            Usuario admin = new Usuario();
            admin.setNome("Administrador");
            admin.setEmail("admin@email.com");

            // ✅ GARANTA QUE SEU CÓDIGO ESTÁ EXATAMENTE ASSIM
            admin.setSenha(passwordEncoder.encode("adminpassword"));

            admin.setTipoPerfil(TipoPerfil.ROLE_ADMINISTRADOR);

            // Se você tiver o campo enabled:
            // admin.setEnabled(true);

            usuarioRepository.save(admin);
        }
    }
}