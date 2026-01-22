package com.zetra.econsig.dto.entidade;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ParamSvcCsaTO</p>
 * <p>Description: Transfer Object dos parametros de serviço da consignatária</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author
 * $Author$
 * $Revision$
 * $Date$
 */
public class ParamSvcCsaTO extends CustomTransferObject {

    public ParamSvcCsaTO() {
        super();
    }

    public ParamSvcCsaTO(String pscCodigo) {
        this();
        setAttribute(Columns.PSC_CODIGO, pscCodigo);
    }

    public ParamSvcCsaTO(ParamSvcCsaTO param) {
        this();
        setAtributos(param.getAtributos());
    }

    // Getter
    public String getPscCodigo() {
        return (String) getAttribute(Columns.PSC_CODIGO);
    }

    public String getSvcCodigo() {
        return (String) getAttribute(Columns.PSC_SVC_CODIGO);
    }

    public String getTpsCodigo() {
        return (String) getAttribute(Columns.TPS_CODIGO);
    }

    public String getCsaCodigo() {
        return (String) getAttribute(Columns.PSC_CSA_CODIGO);
    }

    public String getPscVlr() {
        return (String) getAttribute(Columns.PSC_VLR);
    }

    public String getPscVlrRef() {
        return (String) getAttribute(Columns.PSC_VLR_REF);
    }

    public String getPscDataIniVig() {
        return (String) getAttribute(Columns.PSC_DATA_INI_VIG);
    }

    public String getPscDataFimVig() {
        return (String) getAttribute(Columns.PSC_DATA_FIM_VIG);
    }

    // Setter
    public void setSvcCodigo(String svcCodigo) {
        setAttribute(Columns.PSC_SVC_CODIGO, svcCodigo);
    }

    public void setTpsCodigo(String tpsCodigo) {
        setAttribute(Columns.TPS_CODIGO, tpsCodigo);
    }

    public void setCsaCodigo(String csaCodigo) {
        setAttribute(Columns.PSC_CSA_CODIGO, csaCodigo);
    }

    public void setPscVlr(String pscVlr) {
        setAttribute(Columns.PSC_VLR, pscVlr);
    }

    public void setPscVlrRef(String pscVlrRef) {
        setAttribute(Columns.PSC_VLR_REF, pscVlrRef);
    }

    public void setPscDataIniVig(String pscDataIniVig) {
        setAttribute(Columns.PSC_DATA_INI_VIG, pscDataIniVig);
    }

    public void setPscDataFimVig(String pscDataFimVig) {
        setAttribute(Columns.PSC_DATA_FIM_VIG, pscDataFimVig);
    }
}
