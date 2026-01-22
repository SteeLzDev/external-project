package com.zetra.econsig.dto.entidade;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ParamSistCseTO</p>
 * <p>Description: Transfer Object dos parametros de sistema da consignante</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Igor
 * $Author$
 * $Revision$
 * $Date$
 */
public class ParamSistCseTO extends CustomTransferObject {

    public ParamSistCseTO() {
        super();
    }

    public ParamSistCseTO(String tpcCodigo) {
        this();
        setAttribute(Columns.PSI_TPC_CODIGO, tpcCodigo);
    }

    public ParamSistCseTO(ParamSistCseTO param) {
        this();
        setAtributos(param.getAtributos());
    }

    // Getter
    public String getTpcCodigo() {
        return (String) getAttribute(Columns.PSI_TPC_CODIGO);
    }

    public String getCseCodigo() {
        return (String) getAttribute(Columns.PSI_CSE_CODIGO);
    }

    public String getPsiVlr() {
        return (String) getAttribute(Columns.PSI_VLR);
    }

    // Setter
    public void setCseCodigo(String cseCodigo) {
        setAttribute(Columns.PSI_CSE_CODIGO, cseCodigo);
    }

    public void setPsiVlr(String psiVlr) {
        setAttribute(Columns.PSI_VLR, psiVlr);
    }
}
