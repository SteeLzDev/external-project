package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioGerencialCsaQtdeContratosPorSvcQuery;

public class RelatorioGerencialCsaQtdeContratosPorSvcQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        int maxResultados = 1;
        String periodo = "2023-01-01";
        boolean porcentagem = true;

        RelatorioGerencialCsaQtdeContratosPorSvcQuery query = new RelatorioGerencialCsaQtdeContratosPorSvcQuery(maxResultados, periodo, porcentagem);

        executarConsulta(query);
    }
}

