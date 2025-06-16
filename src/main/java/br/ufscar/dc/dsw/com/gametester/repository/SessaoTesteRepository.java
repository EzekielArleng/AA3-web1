package br.ufscar.dc.dsw.com.gametester.repository;

import br.ufscar.dc.dsw.com.gametester.model.SessaoTeste;
import br.ufscar.dc.dsw.com.gametester.model.StatusSessao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessaoTesteRepository extends JpaRepository<SessaoTeste, Integer> {
    List<SessaoTeste> findByStatus(StatusSessao status);
    List<SessaoTeste> findByTestadorIdOrderByDataHoraCriacaoDesc(Long testador_id);
    List<SessaoTeste> findByProjetoId(Integer projetoId);
    List<SessaoTeste> findByStatusAndTestadorId(StatusSessao status, Long testador_id);

}
