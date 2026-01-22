package com.zetra.econsig.values;

/**
 * <p>Title: StatusBeneficiarioEnum</p>
 * <p>Description: Enumeração de status do beneficiário.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public enum StatusBeneficiarioEnum {
    ATIVO("1"),
    INATIVO("2"),
    EXCLUIDO("3");

    public String sbeCodigo;

    private StatusBeneficiarioEnum(String sbeCodigo) {
        this.sbeCodigo = sbeCodigo;
    }

    public boolean equals(String sbeCodigo) {
        return this.sbeCodigo.equals(sbeCodigo);
    }
}
