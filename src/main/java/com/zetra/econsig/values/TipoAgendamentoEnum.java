package com.zetra.econsig.values;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: TipoAgendamentoEnum</p>
 * <p>Description: Enumeração do tipo de agendamento.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public enum TipoAgendamentoEnum {

    PERIODICO_DIARIO("1"),
    PERIODICO_SEMANAL("2"),
    PERIODICO_MENSAL("3"),
    PERIODICO_ANUAL("4"),
    PERIODICO_5_MIN("5");

    private String codigo;

    private TipoAgendamentoEnum(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    /**
     * Recupera um tipo de agendamento de acordo com o código passado.
     *
     * @param codigo Código do tipo de agendamento que deve ser recuperado.
     * @return Retorna um tipo de agendamento
     *
     * @throws IllegalArgumentException Caso o código do tipo de agendamento informádo seja inválido
     */
    public static TipoAgendamentoEnum recuperaTipoAgendamento(String codigo) {
        TipoAgendamentoEnum tipoAgendamento = null;

        for (TipoAgendamentoEnum tipo : TipoAgendamentoEnum.values()) {
            if (tipo.getCodigo().equals(codigo)) {
                tipoAgendamento = tipo;
                break;
            }
        }

        if (tipoAgendamento == null) {
            throw new IllegalArgumentException("Código informado para o tipo de agendamento inválido!");
        }

        return tipoAgendamento;
    }

    /**
     * Recupera os tipos de agendamentos específicos para os relatório.
     * @return Retorna os tipos de agendamentos.
     */
    public static List<String> getTipoAgendamentoRelatorio() {

        List<String> lstTipoAgendamentoRelatorio = new ArrayList<>();
        lstTipoAgendamentoRelatorio.add(TipoAgendamentoEnum.PERIODICO_DIARIO.getCodigo());
        lstTipoAgendamentoRelatorio.add(TipoAgendamentoEnum.PERIODICO_SEMANAL.getCodigo());
        lstTipoAgendamentoRelatorio.add(TipoAgendamentoEnum.PERIODICO_MENSAL.getCodigo());
        lstTipoAgendamentoRelatorio.add(TipoAgendamentoEnum.PERIODICO_ANUAL.getCodigo());

        return lstTipoAgendamentoRelatorio;

    }
}
