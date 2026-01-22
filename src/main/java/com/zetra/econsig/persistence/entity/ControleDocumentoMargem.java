package com.zetra.econsig.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "tb_controle_documento_margem")
public class ControleDocumentoMargem implements Serializable {


    @Id
    @Column(name = "cdm_codigo", length = 32, nullable = false)
    private String cdmCodigo;

    @Column(name = "rse_codigo", length = 32, nullable = false)
    private String rseCodigo;

    @Column(name = "cdm_local_arquivo", length = 244, nullable = false)
    private String cdmLocalArquivo;

    @Column(name = "cdm_codigo_auth", length = 32, nullable = false)
    private String cdmCodigoAuth;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "cdm_data", nullable = false)
    private Date cdmData;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rse_codigo", referencedColumnName = "rse_codigo", insertable = false, updatable = false)
    private RegistroServidor registroServidor;
}
