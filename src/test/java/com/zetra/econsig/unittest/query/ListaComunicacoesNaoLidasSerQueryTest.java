package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.comunicacao.ListaComunicacoesNaoLidasSerQuery;

public class ListaComunicacoesNaoLidasSerQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaComunicacoesNaoLidasSerQuery query = new ListaComunicacoesNaoLidasSerQuery();
        query.diasAposEnvio = 1;

        executarConsulta(query);
    }
}

