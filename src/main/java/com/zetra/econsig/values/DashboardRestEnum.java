package com.zetra.econsig.values;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;

/**
 * <p>Title: DashboardRestEnum</p>
 * <p>Description: Enum com constantes referentes ao rest de dashboard</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public enum DashboardRestEnum {
    MARGEM_TOTAL_CONSIGNADA("total_consignada"),
    MARGEM_TOTAL_DISPONIVEL("total_disponivel"),
    TOTAL_SERVIDOR_POR_ORGAO("total_servidor_por_orgao"),
    TOTAL_ADE_POR_ORGAO("total_ade_ativas_por_orgao"),
    TIPO_VALOR("1"),
    TIPO_LISTA("2"),
    TIPO_TABELA("3"),
    TIPO_GRAFICO("4");

    private String codigo;

    private DashboardRestEnum(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    public static DashboardRestEnum recuperaDash(String codigo) {
        DashboardRestEnum dash = null;

        for (DashboardRestEnum vlr : DashboardRestEnum.values()) {
            if (vlr.getCodigo().equals(codigo)) {
                dash = vlr;
                break;
            }
        }

        if (dash == null) {
            throw new IllegalArgumentException(ApplicationResourcesHelper.getMessage("mensagem.erro.codigo.dashboard.invalido", (AcessoSistema) null));
        }

        return dash;
    }
}
