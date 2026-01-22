package com.zetra.econsig.dto.entidade;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ParamSvcCseTO</p>
 * <p>Description: Transfer Object dos parametros de serviço da consignante</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Igor
 * $Author$
 * $Revision$
 * $Date$
 */
public class ParamSvcCseTO extends CustomTransferObject {

    public ParamSvcCseTO() {
        super();
    }

    public ParamSvcCseTO(String pseCodigo) {
        this();
        setAttribute(Columns.PSE_CODIGO, pseCodigo);
    }

    public ParamSvcCseTO(String tpsCodigo, String cseCodigo, String svcCodigo) {
        this();
        setSvcCodigo(svcCodigo);
        setCseCodigo(cseCodigo);
        setTpsCodigo(tpsCodigo);
    }

    public ParamSvcCseTO(ParamSvcCseTO param) {
        this();
        setAtributos(param.getAtributos());
    }

    // Getter
    public String getPseCodigo() {
        return (String) getAttribute(Columns.PSE_CODIGO);
    }

    public String getSvcCodigo() {
        return (String) getAttribute(Columns.PSE_SVC_CODIGO);
    }

    public String getTpsCodigo() {
        return (String) getAttribute(Columns.PSE_TPS_CODIGO);
    }

    public String getCseCodigo() {
        return (String) getAttribute(Columns.PSE_CSE_CODIGO);
    }

    public String getPseVlr() {
        return (String) getAttribute(Columns.PSE_VLR);
    }

    public String getPseVlrRef() {
        return (String) getAttribute(Columns.PSE_VLR_REF);
    }

    // Setter
    public void setSvcCodigo(String svcCodigo) {
        setAttribute(Columns.PSE_SVC_CODIGO, svcCodigo);
    }

    public void setTpsCodigo(String tpsCodigo) {
        setAttribute(Columns.PSE_TPS_CODIGO, tpsCodigo);
    }

    public void setCseCodigo(String cseCodigo) {
        setAttribute(Columns.PSE_CSE_CODIGO, cseCodigo);
    }

    public void setPseVlr(String pseVlr) {
        setAttribute(Columns.PSE_VLR, pseVlr);
    }

    public void setPseVlrRef(String pseVlrRef) {
        setAttribute(Columns.PSE_VLR_REF, pseVlrRef);
    }
}
