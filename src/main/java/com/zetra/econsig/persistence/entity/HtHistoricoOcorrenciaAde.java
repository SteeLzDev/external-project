package com.zetra.econsig.persistence.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name= "ht_historico_ocorrencia_ade")
public class HtHistoricoOcorrenciaAde implements Serializable {
    @Serial
    private static final long serialVersionUID = 2L;

    @Id
    @Column(name = "hoa_codigo", nullable = false, length = 32)
    private String hoaCodigo;

    @Column(name = "oca_codigo", nullable = false, length = 32)
    private String ocaCodigo;

    @Column(name = "usu_codigo", nullable = false, length = 32)
    private String usuCodigo;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "hoa_data", nullable = false)
    private Date hoaData;

    @Column(name = "hoa_ip_acesso", nullable = false, length = 45)
    private String hoaIdAcesso;

    @Column(name = "hoa_obs", nullable = false)
    private String hoaObs;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oca_codigo", referencedColumnName = "oca_codigo", insertable = false, updatable = false)
    private HtOcorrenciaAutorizacao htOcorrenciaAutorizacao;
}
