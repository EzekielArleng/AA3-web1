package br.ufscar.dc.dsw.com.gametester.domain;


import org.springframework.security.core.GrantedAuthority;
import br.ufscar.dc.dsw.com.gametester.domain.enums.TipoPerfil;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.io.Serial;
import java.io.Serializable;
import java.util.*;

import jakarta.persistence.*;

@Entity
@Table(name="usuarios")
public class Usuario implements UserDetails, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING) // 3. ESSA ANOTAÇÃO É CRUCIAL!
    @Column(nullable = false, length = 20)
    private TipoPerfil tipoPerfil;

    @ManyToMany(mappedBy = "membros")
    private Set<Projeto> projetos = new HashSet<>();

    public Usuario() {
    }

    public Usuario(Long id, String nome, String email, String senha, TipoPerfil tipoPerfil) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha; // Ao criar/atualizar, esta seria a senha em texto plano antes do hash
        this.tipoPerfil = tipoPerfil;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public TipoPerfil getTipoPerfil() {
        return tipoPerfil;
    }

    public void setTipoPerfil(TipoPerfil tipoPerfil) {
        this.tipoPerfil = tipoPerfil;
    }

    public Set<Projeto> getProjetos() {
        return projetos;
    }

    public void setProjetos(Set<Projeto> projetos) {
        this.projetos = projetos;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // O .name() do nosso enum já retorna "ROLE_ADMINISTRADOR" ou "ROLE_TESTADOR"
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(this.tipoPerfil.name());
        return Collections.singletonList(authority);
    }

    /**
     * Retorna a senha com hash.
     */
    @Override
    public String getPassword() {
        return this.senha;
    }

    /**
     * Retorna o campo que será usado como "username" para login. No nosso caso, o e-mail.
     */
    @Override
    public String getUsername() {
        return this.email;
    }

    // Para os métodos booleanos abaixo, vamos retornar 'true' por padrão.
    // Em um sistema mais complexo, você poderia ter campos no banco para
    // controlar se uma conta está bloqueada, expirada, etc.

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                // Não inclua a senha no toString por segurança
                ", tipoPerfil='" + tipoPerfil + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario)) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(id, usuario.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}