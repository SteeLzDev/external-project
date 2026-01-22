package com.zetra.econsig.dto.entidade;

import java.util.Date;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ParamCnvRseTO</p>
 * <p>Description: Transfer Object dos parametros de convênio por registro servidor</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ParamCnvRseTO extends CustomTransferObject {

    public ParamCnvRseTO() {
        super();
    }

    public ParamCnvRseTO(ParamCnvRseTO param) {
        this();
        setAtributos(param.getAtributos());
    }

    // Getter
    public String getTpsCodigo() {
        return (String) getAttribute(Columns.PCR_TPS_CODIGO);
    }

    public String getRseCodigo() {
        return (String) getAttribute(Columns.PCR_RSE_CODIGO);
    }

    public String getCnvCodigo() {
        return (String) getAttribute(Columns.PCR_CNV_CODIGO);
    }

    public String getPcrVlr() {
        return (String) getAttribute(Columns.PCR_VLR);
    }

    public String getPcrVlrSer() {
        return (String) getAttribute(Columns.PCR_VLR_SER);
    }

    public String getPcrVlrCsa() {
        return (String) getAttribute(Columns.PCR_VLR_CSA);
    }

    public String getPcrVlrCse() {
        return (String) getAttribute(Columns.PCR_VLR_CSE);
    }

    public String getPcrObs() {
        return (String) getAttribute(Columns.PCR_OBS);
    }

    public Date getPcrDataCadastro() {
        return (Date) getAttribute(Columns.PCR_DATA_CADASTRO);
    }


    // Setter
    public void setTpsCodigo(String tpsCodigo) {
        setAttribute(Columns.PCR_TPS_CODIGO, tpsCodigo);
    }

    public void setRseCodigo(String rseCodigo) {
        setAttribute(Columns.PCR_RSE_CODIGO, rseCodigo);
    }

    public void setCnvCodigo(String cnvCodigo) {
        setAttribute(Columns.PCR_CNV_CODIGO, cnvCodigo);
    }

    public void setPcrVlr(String pcrVlr) {
        setAttribute(Columns.PCR_VLR, pcrVlr);
    }

    public void setPcrVlrSer(String pcrVlrSer) {
        setAttribute(Columns.PCR_VLR_SER, pcrVlrSer);
    }

    public void setPcrVlrCsa(String pcrVlrCsa) {
        setAttribute(Columns.PCR_VLR_CSA, pcrVlrCsa);
    }

    public void setPcrVlrCse(String pcrVlrCse) {
        setAttribute(Columns.PCR_VLR_CSE, pcrVlrCse);
    }

    public void setPcrObs(String pcrObs) {
        setAttribute(Columns.PCR_OBS, pcrObs);
    }

    public void setPcrDataCadastro(Date pcrDataCadastro) {
        setAttribute(Columns.PCR_DATA_CADASTRO, pcrDataCadastro);
    }
}
