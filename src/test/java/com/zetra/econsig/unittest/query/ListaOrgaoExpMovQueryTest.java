package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.movimento.ListaOrgaoExpMovQuery;

public class ListaOrgaoExpMovQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaOrgaoExpMovQuery query = new ListaOrgaoExpMovQuery();

        executarConsulta(query);
    }
}

