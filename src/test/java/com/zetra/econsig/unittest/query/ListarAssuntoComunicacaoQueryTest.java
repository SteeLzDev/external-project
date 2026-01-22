package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.comunicacao.ListarAssuntoComunicacaoQuery;

public class ListarAssuntoComunicacaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListarAssuntoComunicacaoQuery query = new ListarAssuntoComunicacaoQuery();
        query.count = false;

        executarConsulta(query);
    }
}

