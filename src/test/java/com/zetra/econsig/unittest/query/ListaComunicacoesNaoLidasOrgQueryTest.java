package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.comunicacao.ListaComunicacoesNaoLidasOrgQuery;

public class ListaComunicacoesNaoLidasOrgQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaComunicacoesNaoLidasOrgQuery query = new ListaComunicacoesNaoLidasOrgQuery();
        query.diasAposEnvio = 1;

        executarConsulta(query);
    }
}

