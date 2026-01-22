package com.zetra.econsig.values;

/**
 * <p>Title: ParamEmailExternoServidorEnum</p>
 * <p>Description: Enumeração de parâmetros de email externo</p>
 * <p>Copyright: Copyright (c) 2024</p>
 * <p>Company: ZetraSoft</p>
 * @author Eduardo Fortes
 */

public enum ParamEmailExternoServidorEnum {

    RESULT_STATUS("status"),
    RESULT_SUCCESS_DATA("data"),
    RESULT_ERROR_DATA("erros"),

    ;

    private final String param;

    private ParamEmailExternoServidorEnum(String param) {
        this.param = param;
    }

    public String getChave() {
        return param;
    }
}
