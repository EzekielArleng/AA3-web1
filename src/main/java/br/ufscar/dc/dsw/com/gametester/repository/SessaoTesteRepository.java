package br.ufscar.dc.dsw.com.gametester.repository;

import br.ufscar.dc.dsw.com.gametester.domain.SessaoTeste;
import br.ufscar.dc.dsw.com.gametester.domain.enums.StatusSessao;
import br.ufscar.dc.dsw.com.gametester.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessaoTesteRepository extends JpaRepository<SessaoTeste, Long> {
    List<SessaoTeste> findByStatus(StatusSessao status);
    List<SessaoTeste> findByProjetoId(Integer projetoId);
    List<SessaoTeste> findByStatusAndTestadorId(StatusSessao status, Long testador_id);
    // Garanta que outros métodos customizados também usem Long
    List<SessaoTeste> findByTestadorOrderByDataHoraCriacaoDesc(Usuario testador);
    List<SessaoTeste> findByProjeto_IdOrderByDataHoraCriacaoDesc(Integer projetoId);
}
