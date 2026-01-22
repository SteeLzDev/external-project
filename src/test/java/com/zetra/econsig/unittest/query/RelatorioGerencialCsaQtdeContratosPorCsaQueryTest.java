package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioGerencialCsaQtdeContratosPorCsaQuery;

public class RelatorioGerencialCsaQtdeContratosPorCsaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        RelatorioGerencialCsaQtdeContratosPorCsaQuery query = new RelatorioGerencialCsaQtdeContratosPorCsaQuery(20, "2023-01-01");
        executarConsulta(query);
    }
}


