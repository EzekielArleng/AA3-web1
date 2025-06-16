package br.ufscar.dc.dsw.com.gametester.service;

import br.ufscar.dc.dsw.com.gametester.dto.AdminSessaoEditDTO;
import br.ufscar.dc.dsw.com.gametester.model.Estrategia;
import br.ufscar.dc.dsw.com.gametester.model.Projeto;
import br.ufscar.dc.dsw.com.gametester.model.SessaoTeste;
import br.ufscar.dc.dsw.com.gametester.model.Usuario;
import br.ufscar.dc.dsw.com.gametester.model.StatusSessao;
import br.ufscar.dc.dsw.com.gametester.model.TipoPerfil;
import br.ufscar.dc.dsw.com.gametester.dto.SessaoCreateDTO;
import br.ufscar.dc.dsw.com.gametester.repository.EstrategiaRepository;
import br.ufscar.dc.dsw.com.gametester.repository.UsuarioRepository;
import br.ufscar.dc.dsw.com.gametester.repository.ProjetoRepository;
import br.ufscar.dc.dsw.com.gametester.repository.SessaoTesteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
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
        Projeto projeto = projetoRepository.findById(dto.projetoId())
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado."));
        Estrategia estrategia = estrategiaRepository.findById(dto.estrategiaId())
                .orElseThrow(() -> new RuntimeException("Estratégia não encontrada."));

        // Regra de autorização: o testador é membro do projeto?
        if (projeto.getMembros().stream().noneMatch(membro -> membro.equals(testador))) {
            throw new SecurityException("Você não é membro deste projeto e não pode criar sessões para ele.");
        }

        SessaoTeste novaSessao = new SessaoTeste();
        novaSessao.setProjeto(projeto);
        novaSessao.setEstrategia(estrategia);
        novaSessao.setTestador(testador);
        novaSessao.setTempoSessaoMinutos(dto.tempoSessaoMinutos());
        novaSessao.setDescricao(dto.descricao());
        novaSessao.setStatus(StatusSessao.CRIADO); // Status inicial é sempre CRIADO

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
    public void iniciarSessao(Long sessaoId, Usuario usuarioLogado) {
        SessaoTeste sessao = getSessaoEChecarPermissao(sessaoId, usuarioLogado);

        // Regra de Negócio: Só pode iniciar uma sessão no estado 'CRIADO'
        if (sessao.getStatus() != StatusSessao.CRIADO) {
            throw new IllegalStateException("A sessão de teste (ID: " + sessaoId + ") não pode ser iniciada pois seu status é '" + sessao.getStatus() + "'.");
        }

        sessao.setStatus(StatusSessao.EM_EXECUCAO);
        sessao.setDataHoraInicio(LocalDateTime.now());
        // A transação do Spring fará o save automaticamente
    }

    /**
     * Finaliza uma sessão de teste, mudando seu status para FINALIZADO.
     * Garante que o usuário tem permissão e que a sessão está no estado correto.
     */
    public void finalizarSessao(Long sessaoId, Usuario usuarioLogado) {
        // CORREÇÃO: Usando o ID Long diretamente e a lógica de permissão
        SessaoTeste sessao = getSessaoEChecarPermissao(sessaoId, usuarioLogado);

        // Regra de Negócio: Só pode finalizar uma sessão 'EM_EXECUCAO'
        if (sessao.getStatus() != StatusSessao.EM_EXECUCAO) {
            throw new IllegalStateException("A sessão só pode ser finalizada se estiver EM EXECUÇÃO.");
        }

        sessao.setStatus(StatusSessao.FINALIZADO);
        // CORREÇÃO: Usando LocalDateTime, que é o tipo da entidade
        sessao.setDataHoraFim(LocalDateTime.now());    }

    /**
     * Busca uma sessão específica para visualização, checando permissões.
     * Usado para carregar a página de detalhes da sessão.
     */
    @Transactional(readOnly = true)
    public SessaoTeste buscarSessaoParaVisualizacao(Long sessaoId, Usuario usuarioLogado) {
        // A lógica de busca e permissão já está encapsulada no método privado.
        return getSessaoEChecarPermissao(sessaoId, usuarioLogado);
    }

    /**
     * Lista todas as sessões de teste pertencentes a um testador específico.
     * Necessário para a página "Minhas Sessões".
     */
    @Transactional(readOnly = true)
    public List<SessaoTeste> listarSessoesPorTestador(Usuario testador) {
        return sessaoTesteRepository.findByTestadorOrderByDataHoraCriacaoDesc(testador);
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
        // Busca as entidades relacionadas
        SessaoTeste sessao = buscarPorId(dto.id());
        Projeto projeto = projetoRepository.findById(dto.projetoId())
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado."));
        Usuario testador = usuarioRepository.findById(dto.testadorId())
                .orElseThrow(() -> new RuntimeException("Testador não encontrado."));
        Estrategia estrategia = estrategiaRepository.findById(dto.estrategiaId())
                .orElseThrow(() -> new RuntimeException("Estratégia não encontrada."));

        // Atualiza os campos na entidade
        sessao.setProjeto(projeto);
        sessao.setTestador(testador);
        sessao.setEstrategia(estrategia);
        sessao.setTempoSessaoMinutos(dto.tempoSessaoMinutos());
        sessao.setDescricao(dto.descricao());
        sessao.setStatus(dto.status());
        sessao.setDataHoraInicio(dto.dataHoraInicio());
        sessao.setDataHoraFim(dto.dataHoraFim());

        // **Lógica de negócio que estava no Servlet, agora no Service**
        if (sessao.getDataHoraInicio() != null && sessao.getDataHoraFim() != null && sessao.getDataHoraFim().isBefore(sessao.getDataHoraInicio())) {
            throw new IllegalArgumentException("A Data/Hora Fim não pode ser anterior à Data/Hora Início.");
        }
        if (sessao.getStatus() == StatusSessao.EM_EXECUCAO && sessao.getDataHoraInicio() == null) {
            throw new IllegalArgumentException("Uma sessão EM EXECUÇÃO precisa de uma Data/Hora de Início.");
        }
        if (sessao.getStatus() == StatusSessao.FINALIZADO && (sessao.getDataHoraInicio() == null || sessao.getDataHoraFim() == null)) {
            throw new IllegalArgumentException("Uma sessão FINALIZADA precisa de Data/Hora de Início e Fim.");
        }

        return sessaoTesteRepository.save(sessao);
    }

    public void excluirSessao(Long id) {
        if (!sessaoTesteRepository.existsById(id)) {
            throw new RuntimeException("Sessão não encontrada para exclusão. ID: " + id);
        }
        try {
            sessaoTesteRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Não é possível excluir a sessão, pois ela possui bugs registrados.");
        }
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