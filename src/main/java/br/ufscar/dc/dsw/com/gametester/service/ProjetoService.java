package br.ufscar.dc.dsw.com.gametester.service;

import br.ufscar.dc.dsw.com.gametester.dto.ProjetoDTO;
import br.ufscar.dc.dsw.com.gametester.domain.Projeto;
import br.ufscar.dc.dsw.com.gametester.domain.Usuario;
import br.ufscar.dc.dsw.com.gametester.repository.ProjetoRepository;
import br.ufscar.dc.dsw.com.gametester.repository.UsuarioRepository; // Supondo que exista
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class ProjetoService {

    @Autowired
    private ProjetoRepository projetoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Projeto salvar(Projeto projeto) {
        return projetoRepository.save(projeto);
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarMembrosDoProjeto(Integer projetoId) {
        Projeto projeto = buscarPorId(projetoId);
        // Retorna a lista de membros já carregada pelo relacionamento
        return new ArrayList<>(projeto.getMembros());
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarUsuariosDisponiveisParaProjeto(Integer projetoId) {
        // Busca todos os usuários
        List<Usuario> todosUsuarios = usuarioRepository.findAll(Sort.by("nome"));
        // Busca os membros atuais
        Set<Usuario> membrosAtuais = buscarPorId(projetoId).getMembros();
        // Remove os membros atuais da lista de todos os usuários
        todosUsuarios.removeAll(membrosAtuais);
        return todosUsuarios;
    }

    public Projeto criarProjeto(ProjetoDTO dto) {
        Projeto projeto = new Projeto();
        projeto.setNome(dto.nome());
        projeto.setDescricao(dto.descricao());
        // A data de criação é gerenciada automaticamente pelo @CreationTimestamp
        return projetoRepository.save(projeto);
    }

    public Projeto editarProjeto(ProjetoDTO dto) {
        Projeto projeto = buscarPorId(dto.id()); // Busca o projeto existente
        projeto.setNome(dto.nome());
        projeto.setDescricao(dto.descricao());
        return projetoRepository.save(projeto);
    }

    @Transactional(readOnly = true)
    public Projeto buscarPorId(Integer id) {
        return projetoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado. ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Projeto> listarTodos(Sort sort) {
        return projetoRepository.findAll(sort);
    }

    @Transactional(readOnly = true)
    public List<Projeto> listarProjetosDoUsuario(Usuario usuario, Sort sort) {
        if (usuario == null) {
            return Collections.emptyList(); // Retorna lista vazia se o usuário for nulo
        }
        return projetoRepository.findByMembros_Id(usuario.getId(), sort);
    }

    public void adicionarMembro(Integer projetoId, Long usuarioId) {
        Projeto projeto = buscarPorId(projetoId);
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado. ID: " + usuarioId));
        projeto.getMembros().add(usuario);
        projetoRepository.save(projeto);
    }

    public void removerMembro(Integer projetoId, Long usuarioId) {
        Projeto projeto = buscarPorId(projetoId);
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado. ID: " + usuarioId));
        projeto.getMembros().remove(usuario);
        projetoRepository.save(projeto);
    }

    public void excluir(Integer id) {
        if (!projetoRepository.existsById(id)) {
            throw new RuntimeException("Projeto não encontrado. ID: " + id);
        }
        try {
            projetoRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            // Substitui a checagem manual do SQLState "23503"
            throw new RuntimeException("Não é possível excluir o projeto, pois ele possui sessões de teste ou outros registros associados.");
        }
    }
}