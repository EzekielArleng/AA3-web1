package br.ufscar.dc.dsw.com.gametester.service;

import br.ufscar.dc.dsw.com.gametester.model.Usuario;
import br.ufscar.dc.dsw.com.gametester.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Injeta o codificador de senhas

    /**
     * Salva um novo usuário. A senha fornecida no objeto deve ser em texto plano.
     * O método se encarrega de fazer o hash antes de salvar.
     */
    public Usuario registrar(Usuario usuario) {
        if (usuarioRepository.existsByEmailIgnoreCase(usuario.getEmail())) {
            throw new RuntimeException("Erro: O e-mail '" + usuario.getEmail() + "' já está cadastrado.");
        }

        // **LÓGICA DE SEGURANÇA CENTRALIZADA**
        String senhaComHash = passwordEncoder.encode(usuario.getSenha());
        usuario.setSenha(senhaComHash);

        return usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmailIgnoreCase(email).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAllByOrderByNomeAsc();
    }

    public void excluir(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado. ID: " + id);
        }
        try {
            usuarioRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            // Substitui a checagem do SQLState "23503"
            throw new RuntimeException("Não é possível excluir o usuário, pois ele possui registros associados.");
        }
    }
}