package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.funcao.ListaPapeisQuery;

public class ListaPapeisQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaPapeisQuery query = new ListaPapeisQuery();

        executarConsulta(query);
    }
}

