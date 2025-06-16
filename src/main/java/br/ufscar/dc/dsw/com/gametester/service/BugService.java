// src/main/java/com/gametester/service/BugService.java
package br.ufscar.dc.dsw.com.gametester.service;

import br.ufscar.dc.dsw.com.gametester.model.Bug;
import br.ufscar.dc.dsw.com.gametester.model.SessaoTeste;
import br.ufscar.dc.dsw.com.gametester.repository.BugRepository;
import br.ufscar.dc.dsw.com.gametester.repository.SessaoTesteRepository; // Supondo que você criou este
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BugService {

    private final BugRepository bugRepository;
    private final SessaoTesteRepository sessaoTesteRepository; // Para buscar a sessão

    @Autowired
    public BugService(BugRepository bugRepository, SessaoTesteRepository sessaoTesteRepository) {
        this.bugRepository = bugRepository;
        this.sessaoTesteRepository = sessaoTesteRepository;
    }

    @Transactional
    public Bug criarBug(Bug bug, Integer sessaoTesteId) {
        // Busca a SessaoTeste para associar ao Bug
        SessaoTeste sessao = sessaoTesteRepository.findById(sessaoTesteId)
                .orElseThrow(() -> new RuntimeException("Sessão de Teste não encontrada com ID: " + sessaoTesteId));
        bug.setSessaoTeste(sessao);
        return bugRepository.save(bug); // O método save serve tanto para criar quanto para atualizar
    }

    @Transactional(readOnly = true)
    public Optional<Bug> buscarPorId(Integer id) {
        return bugRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Bug> listarBugsPorSessao(Integer sessaoTesteId) {
        return bugRepository.findBySessaoTesteIdOrderByDataRegistroAsc(sessaoTesteId);
    }

    @Transactional
    public Bug atualizarBug(Integer id, Bug bugDetails) {
        Bug bugExistente = bugRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bug não encontrado com ID: " + id));

        bugExistente.setDescricao(bugDetails.getDescricao());
        bugExistente.setSeveridade(bugDetails.getSeveridade());
        bugExistente.setScreenshotUrl(bugDetails.getScreenshotUrl());

        return bugRepository.save(bugExistente);
    }

    @Transactional
    public void deletarBug(Integer id) {
        if (!bugRepository.existsById(id)) {
            throw new RuntimeException("Bug não encontrado com ID: " + id);
        }
        bugRepository.deleteById(id);
    }
}