package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioGerencialQtdeContratosPorCategoriaQuery;

public class RelatorioGerencialQtdeContratosPorCategoriaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        int maxResultados = 1;
        String periodo = "2023-01-01";

        RelatorioGerencialQtdeContratosPorCategoriaQuery query = new RelatorioGerencialQtdeContratosPorCategoriaQuery(maxResultados, periodo);
        query.count = false;

        executarConsulta(query);
    }
}

