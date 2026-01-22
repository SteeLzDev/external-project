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
@Table(name = "tb_dashboard_flex_consulta")
public class DashboardFlexConsulta implements Serializable {

    private static final long serialVersionUID = 2L;

    @Id
    @Column(name = "dfo_codigo", nullable = false, length = 32)
    private String dfoCodigo;

    @Column(name = "dfl_codigo", nullable = false, length = 32)
    private String dflCodigo;

    @Column(name = "dfo_titulo", nullable = false, length = 100)
    private String dfoNome;

    @Column(name = "dfo_index", nullable = false, length = 100)
    private String dfoIndex;

    @Column(name = "dfo_tipo_index", nullable = false)
    private Short dfoTipoIndex;

    @Column(name = "dfo_usa_toolbar", nullable = false)
    private Short dfoUsaToolbar;

    @Column(name = "dfo_ativo", nullable = false)
    private Short dfoAtivo;

    @Column(name = "dfo_slice", length = 2147483647)
    private String dfoSlice;

    //--- ENTITY LINKS ( RELATIONSHIP )
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dfl_codigo", referencedColumnName = "dfl_codigo", insertable = false, updatable = false)
    private DashboardFlex dashboardFlex;
}
