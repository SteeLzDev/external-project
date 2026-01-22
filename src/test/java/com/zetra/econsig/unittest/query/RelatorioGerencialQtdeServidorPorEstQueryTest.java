package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioGerencialQtdeServidorPorEstQuery;

public class RelatorioGerencialQtdeServidorPorEstQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        RelatorioGerencialQtdeServidorPorEstQuery query = new RelatorioGerencialQtdeServidorPorEstQuery();

        executarConsulta(query);
    }
}

