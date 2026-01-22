package com.zetra.econsig.dto.entidade;

import java.util.Date;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ParamNseRseTO</p>
 * <p>Description: Transfer Object dos parametros de natureza de serviço por registro servidor</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ParamNseRseTO extends CustomTransferObject {

    public ParamNseRseTO() {
        super();
    }

    public ParamNseRseTO(ParamNseRseTO param) {
        this();
        setAtributos(param.getAtributos());
    }

    // Getter
    public String getTpsCodigo() {
        return (String) getAttribute(Columns.PNR_TPS_CODIGO);
    }

    public String getRseCodigo() {
        return (String) getAttribute(Columns.PNR_RSE_CODIGO);
    }

    public String getNseCodigo() {
        return (String) getAttribute(Columns.PNR_NSE_CODIGO);
    }

    public String getPnrVlr() {
        return (String) getAttribute(Columns.PNR_VLR);
    }

    public String getPnrObs() {
        return (String) getAttribute(Columns.PNR_OBS);
    }

    public String getPnrAlteradoPeloServidor() {
        return (String) getAttribute(Columns.PNR_ALTERADO_PELO_SERVIDOR);
    }

    public Date getPnrDataCadastro() {
        return (Date) getAttribute(Columns.PNR_DATA_CADASTRO);
    }

    // Setter
    public void setTpsCodigo(String tpsCodigo) {
        setAttribute(Columns.PNR_TPS_CODIGO, tpsCodigo);
    }

    public void setRseCodigo(String rseCodigo) {
        setAttribute(Columns.PNR_RSE_CODIGO, rseCodigo);
    }

    public void setNseCodigo(String nseCodigo) {
        setAttribute(Columns.PNR_NSE_CODIGO, nseCodigo);
    }

    public void setPnrVlr(String pnrVlr) {
        setAttribute(Columns.PNR_VLR, pnrVlr);
    }

    public void setPnrObs(String pnrObs) {
        setAttribute(Columns.PNR_OBS, pnrObs);
    }

    public void setPnrAlteradoPeloServidor(String pnrAlteradoPeloServidor) {
        setAttribute(Columns.PNR_ALTERADO_PELO_SERVIDOR, pnrAlteradoPeloServidor);
    }

    public void setPnrDataCadastro(Date pnrDataCadastro) {
        setAttribute(Columns.PNR_DATA_CADASTRO, pnrDataCadastro);
    }
}
