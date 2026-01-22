package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.parcela.ListaStatusParcelaQuery;

public class ListaStatusParcelaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaStatusParcelaQuery query = new ListaStatusParcelaQuery();

        executarConsulta(query);
    }
}

