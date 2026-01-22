package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioGerencialQtdeServidorPorCrsQuery;

public class RelatorioGerencialQtdeServidorPorCrsQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        RelatorioGerencialQtdeServidorPorCrsQuery query = new RelatorioGerencialQtdeServidorPorCrsQuery();

        executarConsulta(query);
    }
}

