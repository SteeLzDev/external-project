package com.zetra.econsig.values;

/**
 * <p>Title: StatusAgendamentoEnum</p>
 * <p>Description: Enumeração de status de agendamento.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public enum StatusAgendamentoEnum {

    EXECUCAO_DIARIA("1"),
    AGUARDANDO_EXECUCAO("2"),
    CONCLUIDO("3"),
    CANCELADO("4"),
    EXECUCAO_SEMANAL("5"),
    EXECUCAO_MENSAL("6"),
    EXECUCAO_ANUAL("7");

    private String codigo;

    private StatusAgendamentoEnum(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    /**
     * Recupera um status de agendamento de acordo com o código passado.
     *
     * @param codigo Código do status de agendamento que deve ser recuperado.
     * @return Retorna um status de agendamento
     *
     * @throws IllegalArgumentException Caso o código do status de agendamento informádo seja inválido
     */
    public static StatusAgendamentoEnum recuperaStatusAgendamento(String codigo) {
        StatusAgendamentoEnum statusAgendamento = null;

        for (StatusAgendamentoEnum status : StatusAgendamentoEnum.values()) {
            if (status.getCodigo().equals(codigo)) {
                statusAgendamento = status;
                break;
            }
        }

        if (statusAgendamento == null) {
            throw new IllegalArgumentException("Código informado para o status de agendamento inválido!");
        }

        return statusAgendamento;
    }

}
