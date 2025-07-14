package br.ufscar.dc.dsw.com.gametester.service;

import br.ufscar.dc.dsw.com.gametester.dto.AdminSessaoEditDTO;
import br.ufscar.dc.dsw.com.gametester.domain.Estrategia;
import br.ufscar.dc.dsw.com.gametester.domain.Projeto;
import br.ufscar.dc.dsw.com.gametester.domain.SessaoTeste;
import br.ufscar.dc.dsw.com.gametester.domain.Usuario;
import br.ufscar.dc.dsw.com.gametester.domain.enums.*;
import br.ufscar.dc.dsw.com.gametester.domain.enums.TipoPerfil;
import br.ufscar.dc.dsw.com.gametester.dto.SessaoCreateDTO;
import br.ufscar.dc.dsw.com.gametester.exception.AuthorizationException;
import br.ufscar.dc.dsw.com.gametester.exception.InvalidDataException;
import br.ufscar.dc.dsw.com.gametester.exception.ResourceNotFoundException;
import br.ufscar.dc.dsw.com.gametester.repository.EstrategiaRepository;
import br.ufscar.dc.dsw.com.gametester.repository.UsuarioRepository;
import br.ufscar.dc.dsw.com.gametester.repository.ProjetoRepository;
import br.ufscar.dc.dsw.com.gametester.repository.SessaoTesteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class SessaoTesteService {

    @Autowired private SessaoTesteRepository sessaoTesteRepository;
    @Autowired private ProjetoRepository projetoRepository;
    @Autowired private EstrategiaRepository estrategiaRepository;
    @Autowired private UsuarioRepository usuarioRepository;

    /**
     * Cria uma nova sessão de teste a partir de um DTO.
     * Garante que o testador logado é membro do projeto.
     */
    public SessaoTeste criarSessao(SessaoCreateDTO dto, Usuario testador) {
        // Cria sessão apenas se existir um projeto ou uma estratégia
        Projeto projeto = projetoRepository.findById(dto.projetoId())
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado."));
        Estrategia estrategia = estrategiaRepository.findById(dto.estrategiaId())
                .orElseThrow(() -> new RuntimeException("Estratégia não encontrada."));


        // Cria sessão apenas se o usuário for membro do projeto
        if (projeto.getMembros().stream().noneMatch(membro -> membro.equals(testador))) {
            throw new SecurityException("Você não é membro deste projeto e não pode criar sessões para ele.");
        }

        // Cria a sessão
        SessaoTeste novaSessao = new SessaoTeste();
        novaSessao.setProjeto(projeto);
        novaSessao.setEstrategia(estrategia);
        novaSessao.setTestador(testador);
        novaSessao.setTempoSessaoMinutos(dto.tempoSessaoMinutos());
        novaSessao.setDescricao(dto.descricao());
        novaSessao.setStatus(StatusSessao.CRIADO); // Status inicial é sempre CRIADO

        // Salva a sessão
        return sessaoTesteRepository.save(novaSessao);
    }

    @Transactional(readOnly = true)
    public SessaoTeste buscarPorId(Long id) {
        return sessaoTesteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sessão de Teste não encontrada. ID: " + id));
    }


    /**
     * Inicia uma sessão de teste, mudando seu status para EM_EXECUCAO.
     * Garante que o usuário tem permissão e que a sessão está no estado correto.
     */
    // Em SessaoTesteService.java

    public SessaoTeste iniciarSessao(Long sessaoId, Usuario usuarioLogado) {
        // Busca a sessão e verifica a permissão
        SessaoTeste sessao = buscarSessaoParaVisualizacao(sessaoId, usuarioLogado);

        // Verifica se o status permite a ação de "iniciar".
        if (sessao.getStatus() == StatusSessao.EM_EXECUCAO || sessao.getStatus() == StatusSessao.FINALIZADO) {
            throw new InvalidDataException("Esta sessão já foi iniciada ou finalizada e não pode ser iniciada novamente.");
        }

        // Inicia a sessão no horário do LocalDateTime
        sessao.setStatus(StatusSessao.EM_EXECUCAO);
        sessao.setDataHoraInicio(LocalDateTime.now());
        return sessaoTesteRepository.save(sessao);
    }


    /**
     * Finaliza uma sessão de teste, mudando seu status para FINALIZADO.
     * Garante que o usuário tem permissão e que a sessão está no estado correto.
     */
    // Em SessaoTesteService.java

    @Transactional
    public SessaoTeste finalizarSessao(Long sessaoId, Usuario usuarioLogado) {
        // Busca a sessão e verifica a permissão
        SessaoTeste sessao = buscarSessaoParaVisualizacao(sessaoId, usuarioLogado);

        // Sessão somente pode ser finalizada se estiver em execução
        if (sessao.getStatus() != StatusSessao.EM_EXECUCAO) {
            throw new InvalidDataException("A sessão não pode ser finalizada, pois seu status atual é: " + sessao.getStatus());
        }

        // Finaliza a sessão no horário do LocalDateTime
        sessao.setStatus(StatusSessao.FINALIZADO);
        sessao.setDataHoraFim(LocalDateTime.now());

        // Salva e retorna a sessão atualizada
        return sessaoTesteRepository.save(sessao);
    }
    /**
     * Busca uma sessão específica para visualização, checando permissões.
     * Usado para carregar a página de detalhes da sessão.
     */
    public SessaoTeste buscarSessaoParaVisualizacao(Long sessaoId, Usuario usuarioLogado) {
        SessaoTeste sessao = sessaoTesteRepository.findByIdWithBugs(sessaoId)
                .orElseThrow(() -> new ResourceNotFoundException("Sessão não encontrada com ID: " + sessaoId));
        if (!sessao.getTestador().getId().equals(usuarioLogado.getId())) {
            throw new AuthorizationException("Você não tem permissão para visualizar esta sessão.");
        }
        return sessao;
    }

    /**
     * Lista todas as sessões de teste pertencentes a um testador específico.
     * Necessário para a página "Minhas Sessões".
     */
    @Transactional(readOnly = true)
    public List<SessaoTeste> listarSessoesPorTestador(Usuario usuarioLogado, Pageable pageable) {
        return sessaoTesteRepository.findByTestador(usuarioLogado, pageable);
    }
    /**
     * Lista todas as sessões de um projeto específico.
     * Garante que o usuário logado tem permissão para ver as sessões do projeto.
     */
    @Transactional(readOnly = true)
    public List<SessaoTeste> listarSessoesPorProjeto(Integer projetoId, Usuario usuarioLogado) {
        Projeto projeto = projetoRepository.findById(projetoId)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado."));

        // Regra de autorização: o usuário é admin ou membro do projeto?
        boolean isAdmin = usuarioLogado.getTipoPerfil() == TipoPerfil.ROLE_ADMINISTRADOR;
        boolean isMembro = projeto.getMembros().stream().anyMatch(membro -> membro.equals(usuarioLogado));

        if (!isAdmin && !isMembro) {
            throw new SecurityException("Você não tem permissão para visualizar as sessões deste projeto.");
        }

        // Supõe que o método existe no repositório.
        return sessaoTesteRepository.findByProjeto_IdOrderByDataHoraCriacaoDesc(projetoId);    }

    @Transactional(readOnly = true)
    public List<SessaoTeste> listarTodasAsSessoes() {
        // Retorna todas as sessões, ordenadas pela mais recente primeiro
        return sessaoTesteRepository.findAll(Sort.by(Sort.Direction.DESC, "dataHoraCriacao"));
    }
    public SessaoTeste editarSessaoPorAdmin(AdminSessaoEditDTO dto) {
        // Busca a entidade principal (já lança ResourceNotFoundException se não encontrar)
        SessaoTeste sessao = buscarPorId(dto.id());

        // Busca as entidades relacionadas usando a exceção correta
        Projeto projeto = projetoRepository.findById(dto.projetoId())
                .orElseThrow(() -> new ResourceNotFoundException("Projeto com ID " + dto.projetoId() + " não encontrado."));
        Usuario testador = usuarioRepository.findById(dto.testadorId())
                .orElseThrow(() -> new ResourceNotFoundException("Testador com ID " + dto.testadorId() + " não encontrado."));
        Estrategia estrategia = estrategiaRepository.findById(dto.estrategiaId())
                .orElseThrow(() -> new ResourceNotFoundException("Estratégia com ID " + dto.estrategiaId() + " não encontrada."));

        // Atualiza os campos na entidade
        sessao.setProjeto(projeto);
        sessao.setTestador(testador);
        sessao.setEstrategia(estrategia);
        sessao.setTempoSessaoMinutos(dto.tempoSessaoMinutos());
        sessao.setDescricao(dto.descricao());
        sessao.setStatus(dto.status());
        sessao.setDataHoraInicio(dto.dataHoraInicio());
        sessao.setDataHoraFim(dto.dataHoraFim());

        // Valida as regras de negócio usando a exceção correta
        if (sessao.getDataHoraInicio() != null && sessao.getDataHoraFim() != null && sessao.getDataHoraFim().isBefore(sessao.getDataHoraInicio())) {
            throw new InvalidDataException("A Data/Hora Fim não pode ser anterior à Data/Hora Início.");
        }
        if (sessao.getStatus() == StatusSessao.EM_EXECUCAO && sessao.getDataHoraInicio() == null) {
            throw new InvalidDataException("Uma sessão EM EXECUÇÃO precisa de uma Data/Hora de Início.");
        }
        if (sessao.getStatus() == StatusSessao.FINALIZADO && (sessao.getDataHoraInicio() == null || sessao.getDataHoraFim() == null)) {
            throw new InvalidDataException("Uma sessão FINALIZADA precisa de Data/Hora de Início e Fim.");
        }

        return sessaoTesteRepository.save(sessao);
    }


    public void excluirSessao(Long id) {
        if (!sessaoTesteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Sessão não encontrada. ID: " + id);
        }
        sessaoTesteRepository.deleteById(id);
    }

    /**
     * Método auxiliar para buscar uma sessão e checar se o usuário logado
     * tem permissão para interagir com ela (seja por ser o dono ou por ser admin).
     * Centraliza a lógica de permissão para evitar repetição de código.
     */
    private SessaoTeste getSessaoEChecarPermissao(Long sessaoId, Usuario usuarioLogado) {
        SessaoTeste sessao = sessaoTesteRepository.findById(sessaoId)
                .orElseThrow(() -> new RuntimeException("Sessão de teste com ID " + sessaoId + " não encontrada."));

        boolean isAdmin = usuarioLogado.getTipoPerfil() == TipoPerfil.ROLE_ADMINISTRADOR;
        boolean isOwner = sessao.getTestador().equals(usuarioLogado);

        if (!isAdmin && !isOwner) {
            throw new SecurityException("Você não tem permissão para operar nesta sessão de teste.");
        }
        return sessao;
    }
}