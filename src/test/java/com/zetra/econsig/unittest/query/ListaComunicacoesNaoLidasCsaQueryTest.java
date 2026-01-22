package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.comunicacao.ListaComunicacoesNaoLidasCsaQuery;

public class ListaComunicacoesNaoLidasCsaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaComunicacoesNaoLidasCsaQuery query = new ListaComunicacoesNaoLidasCsaQuery();
        query.diasAposEnvio = 1;

        executarConsulta(query);
    }
}

