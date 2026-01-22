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
@Table(name = "tb_dashboard_flex_toolbar")
public class DashboardFlexToolbar implements Serializable {

    private static final long serialVersionUID = 2L;

    @Id
    @Column(name = "dft_codigo", nullable = false, length = 32)
    private String dftCodigo;

    @Column(name = "dfo_codigo", nullable = false, length = 32)
    private String dfoCodigo;

    @Column(name = "dft_item", nullable = false, length = 40)
    private String dftItem;

    //--- ENTITY LINKS ( RELATIONSHIP )
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dfo_codigo", referencedColumnName = "dfo_codigo", insertable = false, updatable = false)
    private DashboardFlexConsulta dashboardFlexConsulta;
}
