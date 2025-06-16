package br.ufscar.dc.dsw.com.gametester.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "sessoes_teste")
public class SessaoTeste implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "projeto_id")
    private Projeto projeto;

    @ManyToOne(optional = false)
    @JoinColumn(name = "testador_id")
    private Usuario testador;

    @ManyToOne(optional = false)
    @JoinColumn(name = "estrategia_id")
    private Estrategia estrategia;

    @Column(name = "tempo_sessao_minutos")
    private int tempoSessaoMinutos;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private StatusSessao status;

    @Column(name = "data_hora_criacao")
    private Timestamp dataHoraCriacao;

    @Column(name = "data_hora_inicio")
    private Timestamp dataHoraInicio;

    @Column(name = "data_hora_fim")
    private Timestamp dataHoraFim;

    @OneToMany(mappedBy = "sessaoTeste", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bug> bugs;

    public SessaoTeste() {
    }

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Projeto getProjeto() { return projeto; }
    public void setProjeto(Projeto projeto) { this.projeto = projeto; }

    public Usuario getTestador() { return testador; }
    public void setTestador(Usuario testador) { this.testador = testador; }

    public Estrategia getEstrategia() { return estrategia; }
    public void setEstrategia(Estrategia estrategia) { this.estrategia = estrategia; }

    public int getTempoSessaoMinutos() { return tempoSessaoMinutos; }
    public void setTempoSessaoMinutos(int tempoSessaoMinutos) { this.tempoSessaoMinutos = tempoSessaoMinutos; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public StatusSessao getStatus() { return status; }
    public void setStatus(StatusSessao status) { this.status = status; }

    public Timestamp getDataHoraCriacao() { return dataHoraCriacao; }
    public void setDataHoraCriacao(Timestamp dataHoraCriacao) { this.dataHoraCriacao = dataHoraCriacao; }

    public Timestamp getDataHoraInicio() { return dataHoraInicio; }
    public void setDataHoraInicio(Timestamp dataHoraInicio) { this.dataHoraInicio = dataHoraInicio; }

    public Timestamp getDataHoraFim() { return dataHoraFim; }
    public void setDataHoraFim(Timestamp dataHoraFim) { this.dataHoraFim = dataHoraFim; }

    public List<Bug> getBugs() { return bugs; }
    public void setBugs(List<Bug> bugs) { this.bugs = bugs; }
}
