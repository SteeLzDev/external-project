package com.zetra.econsig.dto.entidade;

import java.util.Date;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ParamSvcRseTO</p>
 * <p>Description: Transfer Object dos parametros de serviço por registro servidor</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ParamSvcRseTO extends CustomTransferObject {

    public ParamSvcRseTO() {
        super();
    }

    public ParamSvcRseTO(ParamSvcRseTO param) {
        this();
        setAtributos(param.getAtributos());
    }

    // Getter
    public String getTpsCodigo() {
        return (String) getAttribute(Columns.PSR_TPS_CODIGO);
    }

    public String getRseCodigo() {
        return (String) getAttribute(Columns.PSR_RSE_CODIGO);
    }

    public String getSvcCodigo() {
        return (String) getAttribute(Columns.PSR_SVC_CODIGO);
    }

    public String getPsrVlr() {
        return (String) getAttribute(Columns.PSR_VLR);
    }

    public String getPsrObs() {
        return (String) getAttribute(Columns.PSR_OBS);
    }

    public String getPsrAlteradoPeloServidor() {
        return (String) getAttribute(Columns.PSR_ALTERADO_PELO_SERVIDOR);
    }

    public Date getPsrDataCadastro() {
        return (Date) getAttribute(Columns.PSR_DATA_CADASTRO);
    }

    // Setter
    public void setTpsCodigo(String tpsCodigo) {
        setAttribute(Columns.PSR_TPS_CODIGO, tpsCodigo);
    }

    public void setRseCodigo(String rseCodigo) {
        setAttribute(Columns.PSR_RSE_CODIGO, rseCodigo);
    }

    public void setSvcCodigo(String svcCodigo) {
        setAttribute(Columns.PSR_SVC_CODIGO, svcCodigo);
    }

    public void setPsrVlr(String psrVlr) {
        setAttribute(Columns.PSR_VLR, psrVlr);
    }

    public void setPsrObs(String psrObs) {
        setAttribute(Columns.PSR_OBS, psrObs);
    }

    public void setPsrAlteradoPeloServidor(String psrAlteradoPeloServidor) {
        setAttribute(Columns.PSR_ALTERADO_PELO_SERVIDOR, psrAlteradoPeloServidor);
    }

    public void setPsrDataCadastro(Date psrDataCadastro) {
        setAttribute(Columns.PSR_DATA_CADASTRO, psrDataCadastro);
    }
}
