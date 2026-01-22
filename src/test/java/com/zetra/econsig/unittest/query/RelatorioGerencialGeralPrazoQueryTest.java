package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioGerencialGeralPrazoQuery;

public class RelatorioGerencialGeralPrazoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        RelatorioGerencialGeralPrazoQuery query = new RelatorioGerencialGeralPrazoQuery();

        executarConsulta(query);
    }
}

