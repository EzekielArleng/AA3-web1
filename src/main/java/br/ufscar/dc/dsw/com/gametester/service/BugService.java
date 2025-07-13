// src/main/java/com/gametester/service/BugService.java
package br.ufscar.dc.dsw.com.gametester.service;

import br.ufscar.dc.dsw.com.gametester.domain.Bug;
import br.ufscar.dc.dsw.com.gametester.domain.SessaoTeste;
import br.ufscar.dc.dsw.com.gametester.domain.Usuario;
import br.ufscar.dc.dsw.com.gametester.domain.enums.Severidade;
import br.ufscar.dc.dsw.com.gametester.domain.enums.StatusSessao;
import br.ufscar.dc.dsw.com.gametester.domain.enums.TipoPerfil;
import br.ufscar.dc.dsw.com.gametester.dto.BugCreateDTO;
import br.ufscar.dc.dsw.com.gametester.exception.AuthorizationException;
import br.ufscar.dc.dsw.com.gametester.exception.ResourceNotFoundException;
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

    // Aceita um DTO, o ID da sessão e o usuário logado.
    public Bug criarBug(BugCreateDTO dto, Long sessaoId, Usuario usuarioLogado) {
        // Lógica de negócio e autorização...
        SessaoTeste sessao = sessaoTesteRepository.findById(sessaoId)
                .orElseThrow(() -> new RuntimeException("Sessão não encontrada."));

        if (!sessao.getTestador().equals(usuarioLogado) && usuarioLogado.getTipoPerfil() != TipoPerfil.ROLE_ADMINISTRADOR) {
            throw new SecurityException("Você não tem permissão para registrar bugs para esta sessão.");
        }
        if (sessao.getStatus() != StatusSessao.EM_EXECUCAO) {
            throw new IllegalStateException("Só é possível registrar bugs em sessões 'EM EXECUÇÃO'.");
        }

        // Converte o DTO para a entidade Bug ANTES de salvar
        Bug novoBug = new Bug();
        novoBug.setSessaoTeste(sessao);
        novoBug.setDescricao(dto.descricao());
        novoBug.setScreenshotUrl(dto.screenshotUrl());

        try {
            Severidade severidadeEnum = Severidade.valueOf(dto.severidade().toUpperCase());
            novoBug.setSeveridade(severidadeEnum);
        } catch (IllegalArgumentException e) {
            // Lança uma exceção se a string do DTO não for um valor válido
            throw new RuntimeException("Severidade inválida: " + dto.severidade());
        }

        return bugRepository.save(novoBug);
    }


    @Transactional(readOnly = true)
    public Bug buscarPorId(Integer id) {
        return bugRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bug não encontrado com ID: " + id));
    }
    @Transactional(readOnly = true)
    public List<Bug> listarBugsPorSessao(Long sessaoTesteId) {
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
    public void deletarBug(Integer bugId, Usuario usuarioLogado) {
        Bug bug = bugRepository.findById(bugId)
                .orElseThrow(() -> new ResourceNotFoundException("Bug não encontrado com ID: " + bugId));
        if (!bug.getSessaoTeste().getTestador().getId().equals(usuarioLogado.getId())) {
            throw new AuthorizationException("Você não tem permissão para excluir este bug.");
        }
        bugRepository.deleteById(bugId);
    }
}