package com.zetra.econsig.report.jasper.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * <p> Title: GerencialInadimplenciaEvolucaoBean</p>
 * <p> Description: POJO para manipulação dos dados recuperados na consulta para o Relatório Gerencial de Inadimplência.</p>
 * <p> Copyright: Copyright (c) 2011 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class GerencialInadimplenciaEvolucaoBean implements Serializable {

    private Date periodoEvolucaoInadimplencia;
    private Float porcEvolucaoInadimplencia;

    public Date getPeriodoEvolucaoInadimplencia() {
        return periodoEvolucaoInadimplencia;
    }

    public void setPeriodoEvolucaoInadimplencia(Date periodoEvolucaoInadimplencia) {
        this.periodoEvolucaoInadimplencia = periodoEvolucaoInadimplencia;
    }

    public Float getPorcEvolucaoInadimplencia() {
        return porcEvolucaoInadimplencia;
    }

    public void setPorcEvolucaoInadimplencia(Float porcEvolucaoInadimplencia) {
        this.porcEvolucaoInadimplencia = porcEvolucaoInadimplencia;
    }
}
