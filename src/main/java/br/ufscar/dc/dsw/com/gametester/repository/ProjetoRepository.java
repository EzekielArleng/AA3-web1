package br.ufscar.dc.dsw.com.gametester.repository;

import br.ufscar.dc.dsw.com.gametester.model.Projeto;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjetoRepository extends JpaRepository<Projeto, Integer> {
    List<Projeto> findByMembros_Id(Long membros_id, Sort sort);
    boolean existsByIdAndMembros_Id(int id, Long membros_id);
}
