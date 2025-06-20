package br.ufscar.dc.dsw.com.gametester.domain;

import br.ufscar.dc.dsw.com.gametester.domain.enums.Severidade;
import jakarta.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "bugs")
public class Bug implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Associação com SessaoTeste (relacionamento @ManyToOne é o mais comum)
    @ManyToOne
    @JoinColumn(name = "sessao_teste_id", nullable = false)
    private SessaoTeste sessaoTeste;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Enumerated(EnumType.STRING) // 3. Anotação para salvar o nome do enum no banco
    @Column(nullable = false, length = 20)
    private Severidade severidade;

    @Column(name = "data_registro")
    private Timestamp dataRegistro;

    @Column(name = "screenshot_url")
    private String screenshotUrl;

    public Bug() {
    }

    public Bug(Integer id, SessaoTeste sessaoTeste, String descricao, Severidade severidade, Timestamp dataRegistro, String screenshotUrl) {
        this.id = id;
        this.sessaoTeste = sessaoTeste;
        this.descricao = descricao;
        this.severidade = severidade;
        this.dataRegistro = dataRegistro;
        this.screenshotUrl = screenshotUrl;
    }

    // Getters e Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public SessaoTeste getSessaoTeste() {
        return sessaoTeste;
    }

    public void setSessaoTeste(SessaoTeste sessaoTeste) {
        this.sessaoTeste = sessaoTeste;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Severidade getSeveridade() {
        return severidade;
    }

    public void setSeveridade(Severidade severidade) {
        this.severidade = severidade;
    }

    public Timestamp getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Timestamp dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public String getScreenshotUrl() {
        return screenshotUrl;
    }

    public void setScreenshotUrl(String screenshotUrl) {
        this.screenshotUrl = screenshotUrl;
    }
}
