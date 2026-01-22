package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.agendamento.ListaOcorrenciaAgendamentoPorPeriodoQuery;

public class ListaOcorrenciaAgendamentoPorPeriodoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        String agdCodigo = "123";
        java.util.List<java.lang.String> tocCodigos = java.util.List.of("1", "2");
        java.util.Date dataInicio = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        java.util.Date dataFim = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();

        ListaOcorrenciaAgendamentoPorPeriodoQuery query = new ListaOcorrenciaAgendamentoPorPeriodoQuery(agdCodigo, tocCodigos, dataInicio, dataFim);

        executarConsulta(query);
    }
}

