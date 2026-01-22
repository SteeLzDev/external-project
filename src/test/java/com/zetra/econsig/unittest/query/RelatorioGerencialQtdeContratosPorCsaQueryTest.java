package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioGerencialQtdeContratosPorCsaQuery;

public class RelatorioGerencialQtdeContratosPorCsaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        int maxResultados = 1;
        String periodo = "2023-01-01";
        boolean status = true;

        RelatorioGerencialQtdeContratosPorCsaQuery query = new RelatorioGerencialQtdeContratosPorCsaQuery(maxResultados, periodo, status);

        executarConsulta(query);
    }
}

