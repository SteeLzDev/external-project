package com.zetra.econsig.dto.parametros;

import java.io.File;
import java.util.Collection;
import java.util.Date;

/**
 * <p>Title: ConfirmarConsignacaoParametros</p>
 * <p>Description: Parâmetros necessários na confirmação de reserva.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ConfirmarConsignacaoParametros extends Parametros {

    private Collection<File> anexos;
    private String anexoObs;
    private Date ocaPeriodo;

    public Collection<File> getAnexos() {
        return anexos;
    }

    public void setAnexos(Collection<File> anexos) {
        this.anexos = anexos;
    }

    public String getAnexoObs() {
        return anexoObs;
    }

    public void setAnexoObs(String anexoObs) {
        this.anexoObs = anexoObs;
    }

    public Date getOcaPeriodo() {
        return ocaPeriodo;
    }

    public void setOcaPeriodo(Date ocaPeriodo) {
        this.ocaPeriodo = ocaPeriodo;
    }

}
