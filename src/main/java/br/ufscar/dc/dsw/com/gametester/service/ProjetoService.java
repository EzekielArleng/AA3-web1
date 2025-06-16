package br.ufscar.dc.dsw.com.gametester.service;

import br.ufscar.dc.dsw.com.gametester.model.Projeto;
import br.ufscar.dc.dsw.com.gametester.model.Usuario;
import br.ufscar.dc.dsw.com.gametester.repository.ProjetoRepository;
import br.ufscar.dc.dsw.com.gametester.repository.UsuarioRepository; // Supondo que exista
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public Projeto buscarPorId(Integer id) {
        return projetoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado. ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Projeto> listarTodos(Sort sort) {
        return projetoRepository.findAll(sort);
    }

    @Transactional(readOnly = true)
    public List<Projeto> listarProjetosDoUsuario(Integer usuarioId, Sort sort) {
        return projetoRepository.findByMembros_Id(Long.valueOf(usuarioId), sort);
    }

    public void adicionarMembro(Integer projetoId, Integer usuarioId) {
        Projeto projeto = buscarPorId(projetoId);
        Usuario usuario = usuarioRepository.findById(usuarioId.longValue())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado. ID: " + usuarioId));
        projeto.getMembros().add(usuario);
        projetoRepository.save(projeto);
    }

    public void removerMembro(Integer projetoId, Integer usuarioId) {
        Projeto projeto = buscarPorId(projetoId);
        Usuario usuario = usuarioRepository.findById(usuarioId.longValue())
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