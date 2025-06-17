package br.ufscar.dc.dsw.com.gametester.repository;
import br.ufscar.dc.dsw.com.gametester.domain.Estrategia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface EstrategiaRepository  extends JpaRepository<Estrategia, Integer> {
    List<Estrategia> findAllByOrderByNomeAsc();
    Optional<Estrategia> findByNomeIgnoreCase(String nome);
    List<Estrategia> findByNomeContainingIgnoreCase(String texto);
}
