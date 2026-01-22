package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioGerencialGeralListaCsaSituacaoObsQuery;

public class RelatorioGerencialGeralListaCsaSituacaoObsQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        final RelatorioGerencialGeralListaCsaSituacaoObsQuery query = new RelatorioGerencialGeralListaCsaSituacaoObsQuery();

        executarConsulta(query);
    }
}

