package com.zetra.econsig.persistence.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "tb_dashboard_flex")
public class DashboardFlex implements Serializable {

    private static final long serialVersionUID = 2L;

    @Id
    @Column(name = "dfl_codigo", nullable = false, length = 32)
    private String dflCodigo;

    @Column(name = "pap_codigo", nullable = false, length = 32)
    private String papCodigo;

    @Column(name = "fun_codigo", nullable = false, length = 32)
    private String funCodigo;

    @Column(name = "dfl_nome", nullable = false, length = 40)
    private String dflNome;

    @Column(name = "dfl_compartilhamento", nullable = false)
    private Short dflCompartilhamento;

    @Column(name = "dfl_ativo", nullable = false)
    private Short dflAtivo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pap_codigo", referencedColumnName = "pap_codigo", insertable = false, updatable = false)
    private Papel papel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fun_codigo", referencedColumnName = "fun_codigo", insertable = false, updatable = false)
    private Funcao funcao;
}
