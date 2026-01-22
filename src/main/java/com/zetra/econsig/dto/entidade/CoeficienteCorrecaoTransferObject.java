package com.zetra.econsig.dto.entidade;

import java.math.BigDecimal;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: CoeficienteCorrecaoTransferObject</p>
 * <p>Description: Transfer Object do Servico</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel e Igor
 * $Author$
 * $Revision$
 * $Date$
 */
public class CoeficienteCorrecaoTransferObject extends CustomTransferObject {

    public CoeficienteCorrecaoTransferObject() {
        super();
    }

    public CoeficienteCorrecaoTransferObject(String tccCodigo) {
        this();
        setAttribute(Columns.TCC_CODIGO, tccCodigo);
    }

    public CoeficienteCorrecaoTransferObject(CoeficienteCorrecaoTransferObject ccr) {
        this();
        setAtributos(ccr.getAtributos());
    }

    // Getter
    public String getTccCodigo() {
        return (String) getAttribute(Columns.TCC_CODIGO);
    }
    
    public String getTccDescricao() {
        return (String) getAttribute(Columns.TCC_DESCRICAO);
    }

    public String getTccFormaCalc() {
        return (String) getAttribute(Columns.TCC_FORMA_CALC);
    }

    public BigDecimal getCcrVlr() {
        return (BigDecimal) getAttribute(Columns.CCR_VLR);
    }

    public Short getCcrMes() {
        return (Short) getAttribute(Columns.CCR_MES);
    }

    public Short getCcrAno() {
        return (Short) getAttribute(Columns.CCR_ANO);
    }

    public BigDecimal getCcrVlrAcumulado() {
        return (BigDecimal) getAttribute(Columns.CCR_VLR_ACUMULADO);
    }

    // Setter    
    public void setTccCodigo(String tccCodigo) {
        setAttribute(Columns.TCC_CODIGO, tccCodigo);
    }

    public void setTccDescricao(String tccDescricao) {
        setAttribute(Columns.TCC_DESCRICAO, tccDescricao);
    }
    
    public void setTccFormaCalc(String tccFormaCalc) {
        setAttribute(Columns.TCC_FORMA_CALC, tccFormaCalc);
    }

    public void setCcrVlr(BigDecimal ccrVlr) {
        setAttribute(Columns.CCR_VLR, ccrVlr);
    }

    public void setCcrMes(Short ccrMes) {
        setAttribute(Columns.CCR_MES, ccrMes);
    }

    public void setCcrAno(Short ccrAno) {
        setAttribute(Columns.CCR_ANO, ccrAno);
    }

    public void setCcrVlrAcumulado(BigDecimal ccrVlrAcumulado) {
        setAttribute(Columns.CCR_VLR_ACUMULADO, ccrVlrAcumulado);
    }
}