package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioGerencialCsaQtdeContratosPorCategoriaQuery;

public class RelatorioGerencialCsaQtdeContratosPorCategoriaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        RelatorioGerencialCsaQtdeContratosPorCategoriaQuery query = new RelatorioGerencialCsaQtdeContratosPorCategoriaQuery(20, "2023-01-01");
        executarConsulta(query);
    }
}


