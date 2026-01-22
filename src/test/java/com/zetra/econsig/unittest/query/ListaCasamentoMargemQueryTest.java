package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.margem.ListaCasamentoMargemQuery;

public class ListaCasamentoMargemQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaCasamentoMargemQuery query = new ListaCasamentoMargemQuery();

        executarConsulta(query);
    }
}

