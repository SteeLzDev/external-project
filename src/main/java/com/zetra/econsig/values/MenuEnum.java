package com.zetra.econsig.values;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;

/**
 * <p>Title: MenuEnum</p>
 * <p>Description: Enumeração do menu.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public enum MenuEnum {

    FAVORITOS("0"),
    OPERACIONAL("1"),
    RELATORIO("2"),
    MANUTENCAO("3"),
    SISTEMA("4"),
    BENEFICIOS("5"),
    AJUDA("6"),
    RESCISAO("7"),
    BUSINESS_INTELLIGENCE("8");

    private String codigo;

    private MenuEnum(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    public static MenuEnum recuperaMenu(String codigo) {
        MenuEnum menu = null;

        for (MenuEnum mnu : MenuEnum.values()) {
            if (mnu.getCodigo().equals(codigo)) {
                menu = mnu;
                break;
            }
        }

        if (menu == null) {
            throw new IllegalArgumentException(ApplicationResourcesHelper.getMessage("mensagem.erro.codigo.menu.invalido", (AcessoSistema) null));
        }

        return menu;
    }
}
