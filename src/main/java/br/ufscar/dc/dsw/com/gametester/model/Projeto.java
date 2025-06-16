package br.ufscar.dc.dsw.com.gametester.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "projetos")
public class Projeto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String nome;

    @Column
    private String descricao;

    @CreationTimestamp
    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "projeto_membro", // Nome da tabela de junção
            joinColumns = @JoinColumn(name = "projeto_id"), // Chave estrangeira para Projeto
            inverseJoinColumns = @JoinColumn(name = "usuario_id") // Chave estrangeira para Usuario
    )
    private Set<Usuario> membros = new HashSet<>();

    public Projeto() {
    }

    public Projeto(int id, String nome, String descricao, LocalDateTime dataCriacao) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.dataCriacao = dataCriacao;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Set<Usuario> getMembros() {
        return membros;
    }

    public void setMembros(Set<Usuario> membros) {
        this.membros = membros;
    }

    @Override
    public String toString() {
        return "Projeto{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", descricao='" + descricao + '\'' +
                ", dataCriacao=" + dataCriacao +
                '}';
    }
}
