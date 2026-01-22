package com.zetra.econsig.persistence.entity;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * JPA entity class for "Formulário de Pesquisa Resposta"
 *
 * @author 
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@ToString
@Table(name = "tb_form_pesquisa_resposta")
public class FormularioPesquisaResposta implements Serializable {

    private static final long serialVersionUID = 2L;

    @Id
    @Column(name = "fpr_codigo", nullable = false, length = 32)
    private String fprCodigo;

    @Column(name = "fpe_codigo", nullable = false, length = 32)
    private String fpeCodigo;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fpr_dt_criacao")
    private Date fprDtCriacao;

    @Column(name = "usu_codigo", nullable = false, length = 32)
    private String usuCodigo;

    @Column(name = "fpr_json", columnDefinition = "JSON")
    private String fprJson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fpe_codigo", referencedColumnName = "fpe_codigo", insertable = false, updatable = false)
    private FormularioPesquisa formularioPesquisa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usu_codigo", referencedColumnName = "usu_codigo", insertable = false, updatable = false)
    private Usuario usuario;

}