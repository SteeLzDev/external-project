package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioIncBeneficiariosPorPeriodoQuery;

public class RelatorioIncBeneficiariosPorPeriodoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        RelatorioIncBeneficiariosPorPeriodoQuery query = new RelatorioIncBeneficiariosPorPeriodoQuery();
        query.codigoOperadora = "123";
        query.benCodigo = "123";
        query.dataInicio = "2023-01-01 00:00:00";
        query.dataFim = "2023-01-01 23:59:59";

        executarConsulta(query);
    }
}

