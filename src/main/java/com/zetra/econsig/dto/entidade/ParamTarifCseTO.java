package com.zetra.econsig.dto.entidade;

import java.math.BigDecimal;
import java.util.Date;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ParamTarifCseTO</p>
 * <p>Description: Transfer Object dos parametros de tarifação da consignante</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Igor
 * $Author$
 * $Revision$
 * $Date$
 */
public class ParamTarifCseTO extends CustomTransferObject {

    public ParamTarifCseTO() {
        super();
    }

    public ParamTarifCseTO(String pcvCodigo) {
        this();
        setAttribute(Columns.PCV_CODIGO, pcvCodigo);
    }

    public ParamTarifCseTO(ParamTarifCseTO param) {
        this();
        setAtributos(param.getAtributos());
    }

    // Getter
    public String getPcvCodigo() {
        return (String) getAttribute(Columns.PCV_CODIGO);
    }

    public String getSvcCodigo() {
        return (String) getAttribute(Columns.PCV_SVC_CODIGO);
    }

    public String getTptCodigo() {
        return (String) getAttribute(Columns.PCV_TPT_CODIGO);
    }

    public Date getPcvDataIniVig() {
        return (Date) getAttribute(Columns.PCV_DATA_INI_VIG);
    }

    public Date getPcvDataFimVig() {
        return (Date) getAttribute(Columns.PCV_DATA_FIM_VIG);
    }

    public Short getPcvAtivo() {
        return (Short) getAttribute(Columns.PCV_ATIVO);
    }

    public BigDecimal getPcvVlr() {
        return (BigDecimal) getAttribute(Columns.PCV_VLR);
    }

    public Integer getPcvBaseCalc() {
        return (Integer) getAttribute(Columns.PCV_BASE_CALC);
    }

    public Integer getPcvFormaCalc() {
        return (Integer) getAttribute(Columns.PCV_FORMA_CALC);
    }

    public Integer getPcvDecimais() {
        return (Integer) getAttribute(Columns.PCV_DECIMAIS);
    }

    public BigDecimal getPcvVlrIni() {
        return (BigDecimal) getAttribute(Columns.PCV_VLR_INI);
    }

    public BigDecimal getPcvVlrFim() {
        return (BigDecimal) getAttribute(Columns.PCV_VLR_FIM);
    }

    public String getCseCodigo() {
        return (String) getAttribute(Columns.PCV_CSE_CODIGO);
    }

    // Setter
    public void setSvcCodigo(String svcCodigo) {
        setAttribute(Columns.PCV_SVC_CODIGO, svcCodigo);
    }

    public void setTptCodigo(String tptCodigo) {
        setAttribute(Columns.PCV_TPT_CODIGO, tptCodigo);
    }

    public void setPcvDataIniVig(Date pcvDataIniVig) {
        setAttribute(Columns.PCV_DATA_INI_VIG, pcvDataIniVig);
    }

    public void setPcvDataFimVig(Date pcvDataFimVig) {
        setAttribute(Columns.PCV_DATA_FIM_VIG, pcvDataFimVig);
    }

    public void setPcvAtivo(Short pcvAtivo) {
        setAttribute(Columns.PCV_ATIVO, pcvAtivo);
    }

    public void setPcvVlr(BigDecimal pcvVlr) {
        setAttribute(Columns.PCV_VLR, pcvVlr);
    }

    public void setPcvBaseCalc(Integer pcvBaseCalc) {
        setAttribute(Columns.PCV_BASE_CALC, pcvBaseCalc);
    }

    public void setPcvFormaCalc(Integer pcvFormaCalc) {
        setAttribute(Columns.PCV_FORMA_CALC, pcvFormaCalc);
    }

    public void setPcvDecimais(Integer pcvDecimais) {
        setAttribute(Columns.PCV_DECIMAIS, pcvDecimais);
    }

    public void setPcvVlrIni(BigDecimal pcvVlrIni) {
        setAttribute(Columns.PCV_VLR_INI, pcvVlrIni);
    }

    public void setPcvVlrFim(BigDecimal pcvVlrFim) {
        setAttribute(Columns.PCV_VLR_FIM, pcvVlrFim);
    }

    public void setCseCodigo(String cseCodigo) {
        setAttribute(Columns.PCV_CSE_CODIGO, cseCodigo);
    }
}
