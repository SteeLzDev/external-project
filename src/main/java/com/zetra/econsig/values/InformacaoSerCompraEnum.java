package com.zetra.econsig.values;

/**
 * <p>Title: InformacaoSerCompraEnum</p>
 * <p>Description: Opções dos parâmetros que definem se senha ou conta bancária são obrigatórios
 * para listar contratos de contras consignatárias na compra.</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public enum InformacaoSerCompraEnum {

    SENHA("S"), 
    CONTA_BANCARIA("C"),
    NADA("N");

    private String codigo;

    private InformacaoSerCompraEnum(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}
