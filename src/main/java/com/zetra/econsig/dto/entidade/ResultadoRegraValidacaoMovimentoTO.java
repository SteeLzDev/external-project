package com.zetra.econsig.dto.entidade;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ResultadoRegraValidacaoMovimentoTO</p>
 * <p>Description: Transfer Object da Resultado Regra Validacao Movimento</p>
 * <p>Copyright: Copyright (c) 2003-2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ResultadoRegraValidacaoMovimentoTO extends CustomTransferObject {

    public ResultadoRegraValidacaoMovimentoTO() {
        super();
    }

    public ResultadoRegraValidacaoMovimentoTO(String rvaCodigo, String rvmCodigo) {
        this();
        setAttribute(Columns.RRV_RVA_CODIGO, rvaCodigo);
        setAttribute(Columns.RRV_RVM_CODIGO, rvmCodigo);
    }

    public ResultadoRegraValidacaoMovimentoTO(ResultadoRegraValidacaoMovimentoTO resultadoRegraValidacaoMovimentoTO) {
        this();
        setAtributos(resultadoRegraValidacaoMovimentoTO.getAtributos());
    }

    // Getter
    public String getRvaCodigo() {
        return (String) getAttribute(Columns.RRV_RVA_CODIGO);
    }

    public String getRvmCodigo() {
        return (String) getAttribute(Columns.RRV_RVM_CODIGO);
    }

    public String getRrvResultado() {
        return (String) getAttribute(Columns.RRV_RESULTADO);
    }

    public String getRrvValorEncontrado() {
        return (String) getAttribute(Columns.RRV_VALOR_ENCONTRADO);
    }

    // Setter
    public void setRrvResultado(String rrvResultado) {
        setAttribute(Columns.RRV_RESULTADO, rrvResultado);
    }

    public void setRrvValorEncontrado(String rrvValorEncontrado) {
        setAttribute(Columns.RRV_VALOR_ENCONTRADO, rrvValorEncontrado);
    }
}
