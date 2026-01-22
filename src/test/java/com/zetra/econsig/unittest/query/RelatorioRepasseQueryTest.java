package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioRepasseQuery;

public class RelatorioRepasseQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        RelatorioRepasseQuery query = new RelatorioRepasseQuery();

        executarConsulta(query);
    }
}

