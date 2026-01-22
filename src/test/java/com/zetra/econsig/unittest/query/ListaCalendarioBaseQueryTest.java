package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.admin.ListaCalendarioBaseQuery;

public class ListaCalendarioBaseQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaCalendarioBaseQuery query = new ListaCalendarioBaseQuery();
        query.cabDiaUtil = "123";
        query.anoMes = "2023-01-01";

        executarConsulta(query);
    }
}

