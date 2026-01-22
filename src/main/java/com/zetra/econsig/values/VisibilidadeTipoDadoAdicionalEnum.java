package com.zetra.econsig.values;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: VisibilidadeTipoDadoAdicionalEnum</p>
 * <p>Description: Enumeração para seleção da visibilidade do tipo de dado adicional de acordo com ação a ser realizada.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public enum VisibilidadeTipoDadoAdicionalEnum {

    // Chaves de configuração que podem ser chamadas externamente
    HOST_A_HOST_LOTE_WEB(CodedValues.TDA_SIM),//123
    HOST_A_HOST("1"),
    LOTE("2"),
    WEB("3");

    // Outras possíveis chaves de configuração mas que só são utilizadas internamente para definir visibilidade de um tipo de dado
    private static String HOST_A_HOST_LOTE = "4";//12
    private static String HOST_A_HOST_WEB = "5";//13
    private static String LOTE_WEB = "6";//23

    private String codigo;

    private VisibilidadeTipoDadoAdicionalEnum(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    /**
     * Recupera uma lista com os códigos de visibilidade para o tipo de dado.
     *
     * 1) HOST_A_HOST_LOTE_WEB requer que sejam recuperados todos os tipos de dados para qualquer configuração:
     *      WEB, LOTE, HOST_A_HOST, LOTE_WEB, HOST_A_HOST_WEB, HOST_A_HOST_LOTE, HOST_A_HOST_LOTE_WEB
     *
     * 2) HOST_A_HOST requer que sejam recuperados os tipos de dados para as seguintes configurações:
     *      HOST_A_HOST, HOST_A_HOST_WEB, HOST_A_HOST_LOTE, HOST_A_HOST_LOTE_WEB
     *
     * 3) LOTE requer que sejam recuperados os tipos de dados para as seguintes configurações:
     *      LOTE, LOTE_WEB, HOST_A_HOST_LOTE, HOST_A_HOST_LOTE_WEB
     *
     * 4) WEB requer que sejam recuperados os tipos de dados para as seguintes configurações
     *      WEB, LOTE_WEB, HOST_A_HOST_WEB, HOST_A_HOST_LOTE_WEB
     *
     * @return retorna uma lista com os códigos de visibilidade para o tipo de dado
     */
    public List<String> getVisibilidade() {
        List<String> visibilidade = new ArrayList<String>();

        switch (this) {
            // HOST_A_HOST_LOTE_WEB requer que sejam recuperados todos os tipos de dados para qualquer configuração
            case HOST_A_HOST_LOTE_WEB:
                visibilidade.add(WEB.getCodigo());
                visibilidade.add(LOTE.getCodigo());
                visibilidade.add(HOST_A_HOST.getCodigo());
                visibilidade.add(LOTE_WEB);
                visibilidade.add(HOST_A_HOST_WEB);
                visibilidade.add(HOST_A_HOST_LOTE);
                visibilidade.add(HOST_A_HOST_LOTE_WEB.getCodigo());

                break;

            // HOST_A_HOST requer que sejam recuperados os tipos de dados para as seguintes configurações
            case HOST_A_HOST:
                visibilidade.add(HOST_A_HOST.getCodigo());
                visibilidade.add(HOST_A_HOST_WEB);
                visibilidade.add(HOST_A_HOST_LOTE);
                visibilidade.add(HOST_A_HOST_LOTE_WEB.getCodigo());

                break;

            // LOTE requer que sejam recuperados os tipos de dados para as seguintes configurações
            case LOTE:
                visibilidade.add(LOTE.getCodigo());
                visibilidade.add(LOTE_WEB);
                visibilidade.add(HOST_A_HOST_LOTE);
                visibilidade.add(HOST_A_HOST_LOTE_WEB.getCodigo());

                break;

            // WEB requer que sejam recuperados os tipos de dados para as seguintes configurações
            case WEB:
                visibilidade.add(WEB.getCodigo());
                visibilidade.add(LOTE_WEB);
                visibilidade.add(HOST_A_HOST_WEB);
                visibilidade.add(HOST_A_HOST_LOTE_WEB.getCodigo());

                break;

            default:
                break;
        }


        return visibilidade;
    }
}
