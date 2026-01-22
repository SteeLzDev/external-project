package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.posto.ListaBloqueioSolicitacaoPorPostoCsaSvcQuery;

public class ListaBloqueioSolicitacaoPorPostoCsaSvcQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaBloqueioSolicitacaoPorPostoCsaSvcQuery query = new ListaBloqueioSolicitacaoPorPostoCsaSvcQuery();
        query.posCodigo = "123";

        executarConsulta(query);
    }
}

