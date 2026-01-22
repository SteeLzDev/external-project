package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.calendario.ListaCalendarioFolhaCseQuery;

public class ListaCalendarioFolhaCseQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaCalendarioFolhaCseQuery query = new ListaCalendarioFolhaCseQuery();
        query.cfcPeriodo = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.anoPeriodo = 1;
        query.cseCodigo = "1";
        query.cfcDataFimMaiorQue = "2023-01-01 01:01:01";
        query.cfcPeriodoMaiorQueIgual = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.count = false;

        executarConsulta(query);
    }
}

