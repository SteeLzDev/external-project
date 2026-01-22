package com.zetra.econsig.dto.entidade;

import java.util.Date;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ParamCsaRseTO</p>
 * <p>Description: Transfer Object dos parametros de consignatária por registro servidor</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ParamCsaRseTO extends CustomTransferObject {

    public ParamCsaRseTO() {
        super();
    }

    public ParamCsaRseTO(ParamCsaRseTO param) {
        this();
        setAtributos(param.getAtributos());
    }

    // Getter
    public String getCsaCodigo() {
        return (String) getAttribute(Columns.PRC_CSA_CODIGO);
    }

    public String getRseCodigo() {
        return (String) getAttribute(Columns.PRC_RSE_CODIGO);
    }

    public String getTpaCodigo() {
        return (String) getAttribute(Columns.PRC_TPA_CODIGO);
    }
    
    public Date getPrcDataCadastro() {
        return (Date) getAttribute(Columns.PRC_DATA_CADASTRO);
    }

    public String getPrcVlr() {
        return (String) getAttribute(Columns.PRC_VLR);
    }

    public String getPrcObs() {
        return (String) getAttribute(Columns.PRC_OBS);
    }


    // Setter
    public void setCsaCodigo(String csaCodigo) {
        setAttribute(Columns.PRC_CSA_CODIGO, csaCodigo);
    }

    public void setRseCodigo(String rseCodigo) {
        setAttribute(Columns.PRC_RSE_CODIGO, rseCodigo);
    }
    
    public void setTpaCodigo(String tpaCodigo) {
        setAttribute(Columns.PRC_TPA_CODIGO, tpaCodigo);
    }
    
    public void setPrcDataCadastro(Date prcDataCadastro) {
        setAttribute(Columns.PRC_DATA_CADASTRO, prcDataCadastro);
    }

    public void setPrcVlr(String prcVlr) {
        setAttribute(Columns.PRC_VLR, prcVlr);
    }

    public void setPrcObs(String prcObs) {
        setAttribute(Columns.PRC_OBS, prcObs);
    }

}
