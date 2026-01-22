package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioGerencialQtdeCorPorCsaQuery;

public class RelatorioGerencialQtdeCorPorCsaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        RelatorioGerencialQtdeCorPorCsaQuery query = new RelatorioGerencialQtdeCorPorCsaQuery();

        executarConsulta(query);
    }
}

