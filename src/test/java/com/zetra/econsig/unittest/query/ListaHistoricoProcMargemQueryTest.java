package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.margem.ListaHistoricoProcMargemQuery;

public class ListaHistoricoProcMargemQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaHistoricoProcMargemQuery query = new ListaHistoricoProcMargemQuery();
        query.estCodigos = java.util.List.of("1", "2");
        query.orgCodigos = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

