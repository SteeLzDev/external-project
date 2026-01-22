package com.zetra.econsig.values;

/**
 * <p>Title: StatusServidorEnum</p>
 * <p>Description: Enumeração de status do servidor.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public enum StatusServidorEnum {
    ATIVO_DIREITO_SUBSIDIO("1"),
    ATIVO_SEM_DIREITO_SUBSIDIO("2"),
    FORA_FOLHA_DIREITO_SUBSIDIO("3"),
    FORA_FOLHA_SEM_DIREITO_SUBSIDIO_VINCULO("4"),
    FORA_FOLHA_SEM_DIREITO_SUBSIDIO_SEM_VINCULO("5");

    public String sseCodigo;

    private StatusServidorEnum(String sseCodigo) {
        this.sseCodigo = sseCodigo;
    }

    public boolean equals(String sseCodigo) {
        return this.sseCodigo.equals(sseCodigo);
    }

    public String getCodigo() {
        return sseCodigo;
    }

    @Override
    public String toString() {
        return sseCodigo.toString();
    }
}
