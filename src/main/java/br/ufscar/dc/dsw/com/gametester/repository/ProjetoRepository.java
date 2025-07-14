package br.ufscar.dc.dsw.com.gametester.repository;

import org.springframework.data.domain.Pageable;
import br.ufscar.dc.dsw.com.gametester.domain.Projeto;
import br.ufscar.dc.dsw.com.gametester.domain.Usuario;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjetoRepository extends JpaRepository<Projeto, Integer> {
    List<Projeto> findByMembros_Id(Long membros_id, Sort sort);
    List<Projeto> findByMembrosContains(Usuario usuario, Pageable pageable);
    boolean existsByIdAndMembros_Id(int id, Long membros_id);
    @Query("SELECT p FROM Projeto p LEFT JOIN FETCH p.membros WHERE :usuario MEMBER OF p.membros")
    List<Projeto> findProjetosByMembroWithMembros(@Param("usuario") Usuario usuario, Pageable pageable);
    @Query("SELECT p FROM Projeto p LEFT JOIN FETCH p.membros WHERE p.id = :id")
    Optional<Projeto> findByIdWithMembros(@Param("id") Integer id);



}
