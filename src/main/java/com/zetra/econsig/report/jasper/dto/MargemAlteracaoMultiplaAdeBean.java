package com.zetra.econsig.report.jasper.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p> Title: MargemAlteracaoMultiplaAdeBean</p>
 * <p> Description: POJO para manipulação dos dados recuperados na Alteração de Múltiplos Contratos.</p>
 * <p> Copyright: Copyright (c) 2011 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class MargemAlteracaoMultiplaAdeBean implements Serializable {

    private Short codigo;
    private String descricao;
    private BigDecimal valorAntes;
    private BigDecimal valorDepois;
    private Boolean exibeMargem;
    private Boolean exibeSemRestricao;

    public MargemAlteracaoMultiplaAdeBean(Short codigo, String descricao, BigDecimal valorAntes, BigDecimal valorDepois, Boolean exibeMargem, Boolean exibeSemRestricao) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.valorAntes = valorAntes;
        this.valorDepois = valorDepois;
        this.exibeMargem = exibeMargem;
        this.exibeSemRestricao = exibeSemRestricao;
    }

    public Short getCodigo() {
        return codigo;
    }

    public void setCodigo(Short codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValorAntes() {
        return valorAntes;
    }

    public void setValorAntes(BigDecimal valorAntes) {
        this.valorAntes = valorAntes;
    }

    public BigDecimal getValorDepois() {
        return valorDepois;
    }

    public void setValorDepois(BigDecimal valorDepois) {
        this.valorDepois = valorDepois;
    }

    public Boolean getExibeMargem() {
        return exibeMargem;
    }

    public void setExibeMargem(Boolean exibeMargem) {
        this.exibeMargem = exibeMargem;
    }

    public Boolean getExibeSemRestricao() {
        return exibeSemRestricao;
    }

    public void setExibeSemRestricao(Boolean exibeSemRestricao) {
        this.exibeSemRestricao = exibeSemRestricao;
    }

}