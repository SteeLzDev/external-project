package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.leilao.ListaDadosPortabilidadeLeilaoQuery;

public class ListaDadosPortabilidadeLeilaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaDadosPortabilidadeLeilaoQuery query = new ListaDadosPortabilidadeLeilaoQuery();
        query.adeCodigo = "731A8D1EAZ564668A4Z0004423D9A1BD";

        executarConsulta(query);
    }
}

