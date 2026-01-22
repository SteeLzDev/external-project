package com.zetra.econsig.values;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title: CanalEnum</p>
 * <p>Description: Enumeração para canais de acesso.</p>
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author: marlon.silva $
 * $Revision: 26978 $
 * $Date: 2019-06-18 12:11:00 -0300 (ter, 18 jun 2019) $
 */

public enum CanalEnum {

    WEB("1"),
    SOAP("2"),
    REST("3");

    private String codigo;

    private CanalEnum(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    // Reverse-lookup map para buscar um canal através de um codigo
    private static final Map<String, CanalEnum> lookup = new HashMap<String, CanalEnum>();

    static {
        for (CanalEnum c : CanalEnum.values()) {
            lookup.put(c.getCodigo(), c);
        }
    }

    public static CanalEnum get(String codigo) {
        return lookup.get(codigo);
    }
}
