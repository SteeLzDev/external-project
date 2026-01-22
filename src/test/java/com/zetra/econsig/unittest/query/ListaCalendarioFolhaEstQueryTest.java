package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.calendario.ListaCalendarioFolhaEstQuery;

public class ListaCalendarioFolhaEstQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaCalendarioFolhaEstQuery query = new ListaCalendarioFolhaEstQuery();
        query.cfePeriodo = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.anoPeriodo = 1;
        query.estCodigo = "751F8080808080808080808080809680";
        query.cfeDataFimMaiorQue = "2023-01-01 01:01:01";
        query.cfePeriodoMaiorQueIgual = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.count = false;

        executarConsulta(query);
    }
}

