package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servidor.ListaNivelEscolaridadeQuery;

public class ListaNivelEscolaridadeQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaNivelEscolaridadeQuery query = new ListaNivelEscolaridadeQuery();
        query.nesCodigo = "123";

        executarConsulta(query);
    }
}

