package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.log.ListaTipoLogQuery;

public class ListaTipoLogQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaTipoLogQuery query = new ListaTipoLogQuery();

        executarConsulta(query);
    }
}

