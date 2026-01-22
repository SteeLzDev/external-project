package com.zetra.econsig.dto.entidade;

import java.sql.Timestamp;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: OcorrenciaConsignatariaTransferObject</p>
 * <p>Description: TransferObject de ocorrencias consignatária</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class OcorrenciaConsignatariaTransferObject extends CustomTransferObject {

    public OcorrenciaConsignatariaTransferObject() {
        super();
    }

    public OcorrenciaConsignatariaTransferObject(String occCodigo) {
        this();
        setAttribute(Columns.OCC_CODIGO, occCodigo);
    }

    public OcorrenciaConsignatariaTransferObject(OcorrenciaConsignatariaTransferObject occConsignataria) {
        this();
        setAtributos(occConsignataria.getAtributos());
    }

    // Getter
    public String getOccCodigo() {
        return (String) getAttribute(Columns.OCC_CODIGO);
    }

    public String getCsaCodigo() {
        return (String) getAttribute(Columns.OCC_CSA_CODIGO);
    }

    public Timestamp getOccData() {
        return (Timestamp) getAttribute(Columns.OCC_DATA);
    }

    public String getOccObs() {
        return (String) getAttribute(Columns.OCC_OBS);
    }

    public String getTocCodigo() {
        return (String) getAttribute(Columns.OCC_TOC_CODIGO);
    }

    public String getUsuCodigo() {
        return (String) getAttribute(Columns.OCC_USU_CODIGO);
    }

    public String getTpeCodigo() {
        return (String) getAttribute(Columns.OCC_TPE_CODIGO);
    }

    // Setter
    public void setCsaCodigo(String csaCodigo) {
        setAttribute(Columns.OCC_CSA_CODIGO, csaCodigo);
    }

    public void setOccObs(String occObs) {
        setAttribute(Columns.OCC_OBS, occObs);
    }

    public void setOccData(Timestamp occData) {
        setAttribute(Columns.OCC_DATA, occData);
    }

    public void setTocCodigo(String tocCodigo) {
        setAttribute(Columns.OCC_TOC_CODIGO, tocCodigo);
    }

    public void setUsuCodigo(String usuCodigo) {
        setAttribute(Columns.OCC_USU_CODIGO, usuCodigo);
    }
    
    public void setTpeCodigo(String tpeCodigo) {
        setAttribute(Columns.OCC_TPE_CODIGO, tpeCodigo);
    }
}
