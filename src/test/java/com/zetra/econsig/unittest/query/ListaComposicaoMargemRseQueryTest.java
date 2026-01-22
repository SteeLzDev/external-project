package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servidor.ListaComposicaoMargemRseQuery;

public class ListaComposicaoMargemRseQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaComposicaoMargemRseQuery query = new ListaComposicaoMargemRseQuery();
        query.rseCodigo = "123";

        executarConsulta(query);
    }
}

