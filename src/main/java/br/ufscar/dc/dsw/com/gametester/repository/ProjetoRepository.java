package br.ufscar.dc.dsw.com.gametester.repository;

import org.springframework.data.domain.Pageable;
import br.ufscar.dc.dsw.com.gametester.domain.Projeto;
import br.ufscar.dc.dsw.com.gametester.domain.Usuario;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjetoRepository extends JpaRepository<Projeto, Integer> {
    List<Projeto> findByMembros_Id(Long membros_id, Sort sort);
    List<Projeto> findByMembrosContains(Usuario usuario, Pageable pageable);
    boolean existsByIdAndMembros_Id(int id, Long membros_id);
}
