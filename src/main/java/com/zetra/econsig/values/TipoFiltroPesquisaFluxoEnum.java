package com.zetra.econsig.values;

/**
 * <p>Title: FiltroPesquisaFluxoEnum</p>
 * <p>Description: Enumeração de filtro de pesquisa.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public enum TipoFiltroPesquisaFluxoEnum {

    FILTRO_TUDO ("1", "rotulo.fluxo.portal.tudo"),
    FILTRO_CIDADE ("2", "rotulo.fluxo.portal.cidade"),
    FILTRO_BAIRRO ("3", "rotulo.fluxo.portal.bairro"),
    FILTRO_BENEFICIO ("4", "rotulo.fluxo.portal.beneficio");

    private String codigo;
    private String rotulo;

    private TipoFiltroPesquisaFluxoEnum(String codigo, String rotulo) {
        this.codigo = codigo;
        this.rotulo = rotulo;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getRotulo() {
        return rotulo;
    }

    /**
     * Recupera tipo do filtro de acordo com o código passado.
     */
    public static TipoFiltroPesquisaFluxoEnum recuperaTipo(String codigo) {
        TipoFiltroPesquisaFluxoEnum agendamento = null;

        for (TipoFiltroPesquisaFluxoEnum tipo : TipoFiltroPesquisaFluxoEnum.values()) {
            if (tipo.getCodigo().equals(codigo)) {
                agendamento = tipo;
                break;
            }
        }

        return agendamento;

    }

}
