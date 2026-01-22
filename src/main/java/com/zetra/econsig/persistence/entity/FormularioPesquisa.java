package com.zetra.econsig.persistence.entity;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA entity class for "Formulário de Pesquisa"
 *
 * @author 
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tb_form_pesquisa")
@EqualsAndHashCode
public class FormularioPesquisa implements Serializable {

    private static final long serialVersionUID = 2L;

    @Id
    @Column(name = "fpe_codigo", nullable = false, length = 32)
    private String fpeCodigo;

    @Column(name = "fpe_nome", nullable = false, length = 100)
    private String fpeNome;

    @Column(name = "fpe_bloqueia_sistema")
    private Boolean fpeBloqueiaSistema;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fpe_dt_criacao")
    private Date fpeDtCriacao;

    @Temporal(TemporalType.DATE)
    @Column(name = "fpe_dt_fim")
    private Date fpeDtFim;

    @Column(name = "fpe_publicado")
    private Boolean fpePublicado;

    @Column(name = "fpe_json", columnDefinition = "JSON")
    private String fpeJson;

    public FormularioPesquisa(String fpeCodigo) {
        this.fpeCodigo = fpeCodigo;
    }

}