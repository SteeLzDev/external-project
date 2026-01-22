package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.admin.ListaVencimentoQuery;

public class ListaVencimentoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaVencimentoQuery query = new ListaVencimentoQuery();
        query.vctIdentificador = "123";

        executarConsulta(query);
    }
}

