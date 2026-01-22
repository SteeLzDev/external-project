package com.zetra.econsig.values;

/**
 * <p>Title: StatusContratoBeneficioEnum</p>
 * <p>Description: Enumeração do status do contrato de benefício.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public enum StatusContratoBeneficioEnum {

    SOLICITADO("1"),
    AGUARD_INCLUSAO_OPERADORA("2"),
    ATIVO("3"),
    CANCELAMENTO_SOLICITADO("4"),
    AGUARD_EXCLUSAO_OPERADORA("5"),
    CANCELADO("6"),
    CANCELAMENTO_SOLICITADO_BENEFICIARIO("7");

    private String codigo;

    private StatusContratoBeneficioEnum(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}
