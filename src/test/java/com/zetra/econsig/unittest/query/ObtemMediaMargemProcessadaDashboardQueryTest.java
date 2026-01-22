package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.dashboardprocessamento.ObtemMediaMargemProcessadaDashboardQuery;

public class ObtemMediaMargemProcessadaDashboardQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemMediaMargemProcessadaDashboardQuery query = new ObtemMediaMargemProcessadaDashboardQuery();
        query.tipoEntidade = "ORG";
        query.codigoEntidade = "123";

        executarConsulta(query);
    }
}

