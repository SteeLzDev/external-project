package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.margem.ListaMargemQuery;

public class ListaMargemQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaMargemQuery query = new ListaMargemQuery();
        query.isRaiz = true;
        query.alteracaoMultiplaAde = true;
        query.marCodigoPai = "123";

        executarConsulta(query);
    }
}

