package br.ufscar.dc.dsw.com.gametester.repository;

import br.ufscar.dc.dsw.com.gametester.model.SessaoTeste;
import br.ufscar.dc.dsw.com.gametester.model.StatusSessao;
import br.ufscar.dc.dsw.com.gametester.model.Usuario;
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
