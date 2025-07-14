package br.ufscar.dc.dsw.com.gametester.repository;
import br.ufscar.dc.dsw.com.gametester.domain.Projeto;
import org.springframework.data.domain.Pageable;

import br.ufscar.dc.dsw.com.gametester.domain.SessaoTeste;
import br.ufscar.dc.dsw.com.gametester.domain.enums.StatusSessao;
import br.ufscar.dc.dsw.com.gametester.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SessaoTesteRepository extends JpaRepository<SessaoTeste, Long> {
    List<SessaoTeste> findByStatus(StatusSessao status);
    List<SessaoTeste> findByProjetoId(Integer projetoId);
    List<SessaoTeste> findByStatusAndTestadorId(StatusSessao status, Long testador_id);
    // Garanta que outros métodos customizados também usem Long
    List<SessaoTeste> findByTestadorOrderByDataHoraCriacaoDesc(Usuario testador);
    List<SessaoTeste> findByProjeto_IdOrderByDataHoraCriacaoDesc(Integer projetoId);
    List<SessaoTeste> findByTestador(Usuario testador, Pageable pageable);

    @Query("SELECT count(s) FROM SessaoTeste s WHERE s.dataHoraCriacao BETWEEN :inicioPeriodo AND :fimPeriodo")
    long countSessoesNoPeriodo(@Param("inicioPeriodo") LocalDateTime inicioPeriodo, @Param("fimPeriodo") LocalDateTime fimPeriodo);

    @Query("SELECT s FROM SessaoTeste s LEFT JOIN FETCH s.bugs WHERE s.id = :id")
    Optional<SessaoTeste> findByIdWithBugs(@Param("id") Long id);
}

