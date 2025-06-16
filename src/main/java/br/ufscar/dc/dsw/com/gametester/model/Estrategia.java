package br.ufscar.dc.dsw.com.gametester.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "estrategias")
public class Estrategia implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(columnDefinition = "TEXT")
    private String exemplos;

    @Column(columnDefinition = "TEXT")
    private String dicas;

    // Imagens podem ser implementadas futuramente com uma tabela separada ou como JSON/texto

    public Estrategia() {
    }

    public Estrategia(int id, String nome, String descricao, String exemplos, String dicas) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.exemplos = exemplos;
        this.dicas = dicas;
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

    public String getExemplos() {
        return exemplos;
    }

    public void setExemplos(String exemplos) {
        this.exemplos = exemplos;
    }

    public String getDicas() {
        return dicas;
    }

    public void setDicas(String dicas) {
        this.dicas = dicas;
    }

    @Override
    public String toString() {
        return "Estrategia{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", descricao='" + descricao + '\'' +
                ", exemplos='" + exemplos + '\'' +
                ", dicas='" + dicas + '\'' +
                '}';
    }
}
