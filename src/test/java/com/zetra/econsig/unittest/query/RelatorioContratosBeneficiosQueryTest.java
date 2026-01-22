package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioContratosBeneficiosQuery;

public class RelatorioContratosBeneficiosQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        RelatorioContratosBeneficiosQuery query = new RelatorioContratosBeneficiosQuery();
        query.dataIni = "2023-01-01 00:00:00";
        query.dataFim = "2023-01-01 23:59:59";
        query.status = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

