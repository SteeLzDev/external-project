package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioGerencialQtdeServidorPorTipoQuery;

public class RelatorioGerencialQtdeServidorPorTipoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        RelatorioGerencialQtdeServidorPorTipoQuery query = new RelatorioGerencialQtdeServidorPorTipoQuery();

        executarConsulta(query);
    }
}

