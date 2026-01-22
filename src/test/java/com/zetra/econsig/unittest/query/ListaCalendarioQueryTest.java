package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.admin.ListaCalendarioQuery;

public class ListaCalendarioQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaCalendarioQuery query = new ListaCalendarioQuery();
        query.calDiaUtil = "123";
        query.anoMes = "2023-01-01";

        executarConsulta(query);
    }
}

