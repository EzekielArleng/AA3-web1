package br.ufscar.dc.dsw.com.gametester.service;

import br.ufscar.dc.dsw.com.gametester.dto.AdminUsuarioCreateDTO;
import br.ufscar.dc.dsw.com.gametester.dto.AdminUsuarioEditDTO;
import br.ufscar.dc.dsw.com.gametester.dto.PerfilEditDTO;
import br.ufscar.dc.dsw.com.gametester.dto.SenhaChangeDTO;
import br.ufscar.dc.dsw.com.gametester.domain.Usuario;
import br.ufscar.dc.dsw.com.gametester.exception.DataConflictException;
import br.ufscar.dc.dsw.com.gametester.exception.InvalidDataException;
import br.ufscar.dc.dsw.com.gametester.exception.ResourceNotFoundException;
import br.ufscar.dc.dsw.com.gametester.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional; // Import para a sugestão abaixo

@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario registrar(Usuario usuario) {
        if (usuarioRepository.existsByEmailIgnoreCase(usuario.getEmail())) {
            throw new DataConflictException("Erro: O e-mail '" + usuario.getEmail() + "' já está cadastrado.");
        }
        String senhaComHash = passwordEncoder.encode(usuario.getSenha());
        usuario.setSenha(senhaComHash);
        return usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado. ID: " + id));
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorEmail(String email) {
        // Ponto de atenção: Retornar Optional é mais seguro que retornar null.
        return usuarioRepository.findByEmailIgnoreCase(email);
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll(Sort.by("nome"));
    }

    public Usuario criarUsuarioPorAdmin(AdminUsuarioCreateDTO dto) {
        if (!dto.senha().equals(dto.confirmaSenha())) {
            throw new InvalidDataException("As senhas não coincidem.");
        }
        if (usuarioRepository.existsByEmailIgnoreCase(dto.email())) {
            throw new DataConflictException("O e-mail '" + dto.email() + "' já está cadastrado.");
        }
        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(dto.nome());
        novoUsuario.setEmail(dto.email().toLowerCase());
        novoUsuario.setSenha(passwordEncoder.encode(dto.senha()));
        novoUsuario.setTipoPerfil(dto.tipoPerfil());
        return usuarioRepository.save(novoUsuario);
    }

    public Usuario editarUsuarioPorAdmin(AdminUsuarioEditDTO dto) {
        boolean atualizarSenha = dto.novaSenha() != null && !dto.novaSenha().isEmpty();
        if (atualizarSenha && !dto.novaSenha().equals(dto.confirmaNovaSenha())) {
            throw new InvalidDataException("As senhas não coincidem.");
        }
        Usuario usuario = buscarPorId(dto.id());
        if (!dto.email().equalsIgnoreCase(usuario.getEmail()) && usuarioRepository.existsByEmailIgnoreCase(dto.email())) {
            throw new DataConflictException("O e-mail '" + dto.email() + "' já está em uso por outra conta.");
        }
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email().toLowerCase());
        usuario.setTipoPerfil(dto.tipoPerfil());
        if (atualizarSenha) {
            usuario.setSenha(passwordEncoder.encode(dto.novaSenha()));
        }
        return usuarioRepository.save(usuario);
    }

    public void excluirUsuario(Long id, Usuario adminLogado) {
        if (id.equals(adminLogado.getId())) {
            throw new InvalidDataException("Um administrador não pode excluir a si mesmo.");
        }
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuário a ser excluído não encontrado. ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    /**
     * ATUALIZADO: Agora retorna o objeto Usuario salvo.
     */
    public Usuario atualizarPerfil(Usuario usuarioLogado, PerfilEditDTO dto) {
        if (!dto.email().equalsIgnoreCase(usuarioLogado.getEmail())) {
            if (usuarioRepository.existsByEmailIgnoreCase(dto.email())) {
                throw new DataConflictException("O e-mail '" + dto.email() + "' já está em uso por outra conta.");
            }
        }
        usuarioLogado.setNome(dto.nome());
        usuarioLogado.setEmail(dto.email().toLowerCase());
        return usuarioRepository.save(usuarioLogado);
    }

    public void alterarSenha(Usuario usuarioLogado, SenhaChangeDTO dto) {
        if (!dto.novaSenha().equals(dto.confirmaNovaSenha())) {
            throw new InvalidDataException("A nova senha e a confirmação não coincidem.");
        }
        if (!passwordEncoder.matches(dto.senhaAtual(), usuarioLogado.getPassword())) {
            throw new InvalidDataException("A 'Senha Atual' está incorreta.");
        }
        usuarioLogado.setSenha(passwordEncoder.encode(dto.novaSenha()));
        usuarioRepository.save(usuarioLogado);
    }

    public void excluir(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuário não encontrado. ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }
}