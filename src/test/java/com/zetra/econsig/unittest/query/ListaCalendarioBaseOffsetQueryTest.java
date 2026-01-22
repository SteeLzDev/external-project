package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.admin.ListaCalendarioBaseOffsetQuery;

public class ListaCalendarioBaseOffsetQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaCalendarioBaseOffsetQuery query = new ListaCalendarioBaseOffsetQuery();
        query.mesDiff = true;
        query.dayDiff = true;
        query.dateOffset = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.diffOffset = 1;

        executarConsulta(query);
    }
}

