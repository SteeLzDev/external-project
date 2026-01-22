package com.zetra.econsig.values;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title: OperacaoValidacaoTotpEnum</p>
 * <p>Description: Enumeração para operações que podem validar TOTP.</p>
 * <p>Copyright: Copyright (c) 2024</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */

public enum OperacaoValidacaoTotpEnum {

    AUTORIZACAO_OPERACAO_SENSIVEL("1"),
    AUTENTICACAO_SISTEMA("2"),
    AMBOS("3");

    private final String codigo;

    private OperacaoValidacaoTotpEnum(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    // Reverse-lookup map para buscar o enum através de um codigo
    private static final Map<String, OperacaoValidacaoTotpEnum> lookup = new HashMap<>();

    static {
        for (OperacaoValidacaoTotpEnum c : OperacaoValidacaoTotpEnum.values()) {
            lookup.put(c.getCodigo(), c);
        }
    }

    public static OperacaoValidacaoTotpEnum get(String codigo) {
        return lookup.get(codigo);
    }
}
