package br.ufscar.dc.dsw.com.gametester.repository;

import br.ufscar.dc.dsw.com.gametester.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmailIgnoreCase(String email);
    List<Usuario> findAllByOrderByNomeAsc();
    boolean existsByEmailIgnoreCase(String email);
}
