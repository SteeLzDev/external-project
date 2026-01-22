package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.dashboardprocessamento.ListarHistoricoMediaMargemDashboardQuery;

public class ListarHistoricoMediaMargemDashboardQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListarHistoricoMediaMargemDashboardQuery query = new ListarHistoricoMediaMargemDashboardQuery();
        query.tipoEntidade = "ORG";
        query.codigoEntidade = "123";
        query.periodoIni = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.periodoFim = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.marCodigo = 1;

        executarConsulta(query);
    }
}

