package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.comunicacao.ListaComunicacoesNaoLidasCseQuery;

public class ListaComunicacoesNaoLidasCseQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaComunicacoesNaoLidasCseQuery query = new ListaComunicacoesNaoLidasCseQuery();
        query.diasAposEnvio = 1;

        executarConsulta(query);
    }
}

