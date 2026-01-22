package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.dashboardprocessamento.ListarBlocosProcessamentoDashboardQuery;

public class ListarBlocosProcessamentoDashboardQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListarBlocosProcessamentoDashboardQuery query = new ListarBlocosProcessamentoDashboardQuery();
        query.tipoEntidade = "ORG";
        query.codigoEntidade = "123";
        query.bprPeriodo = "2023-01-01";
        query.tbpCodigos = java.util.List.of("1", "2");
        query.sbpCodigos = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

