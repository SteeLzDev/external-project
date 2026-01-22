package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioGerencialCseQuery;

public class RelatorioGerencialCseQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        RelatorioGerencialCseQuery query = new RelatorioGerencialCseQuery();

        executarConsulta(query);
    }
}

