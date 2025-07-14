package br.ufscar.dc.dsw;

// ... (seus imports)
import br.ufscar.dc.dsw.com.gametester.domain.Usuario;
import br.ufscar.dc.dsw.com.gametester.domain.enums.TipoPerfil;
import br.ufscar.dc.dsw.com.gametester.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class GameTesterSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(GameTesterSystemApplication.class, args);
	}
	@Bean
	public CommandLineRunner init(UsuarioRepository usuarioRepo, ProjetoRepository projetoRepo,
								  EstrategiaRepository estrategiaRepo, PasswordEncoder passwordEncoder) {
		return args -> {
			if (usuarioRepo.count() == 0) {
				System.out.println("### Criando dados iniciais com senhas CRIPTOGRAFADAS... ###");

				Usuario admin = new Usuario();
				admin.setNome("Administrador");
				admin.setEmail("admin@email.com");
				// Criptografa a senha "admin" antes de salvar
				admin.setSenha(passwordEncoder.encode("admin"));
				admin.setTipoPerfil(TipoPerfil.ROLE_ADMINISTRADOR);
				usuarioRepo.save(admin);

				Usuario tester = new Usuario();
				tester.setNome("Testador Padrão");
				tester.setEmail("tester@email.com");
				// Criptografa a senha "tester" antes de salvar
				tester.setSenha(passwordEncoder.encode("tester"));
				tester.setTipoPerfil(TipoPerfil.ROLE_TESTADOR);
				usuarioRepo.save(tester);

				// ... (criação de projetos, estratégias, etc.) ...

				System.out.println("### Dados iniciais criados corretamente! ###");
			}
		};
	}
}