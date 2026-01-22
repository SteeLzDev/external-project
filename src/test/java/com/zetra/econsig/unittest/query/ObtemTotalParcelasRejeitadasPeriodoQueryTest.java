package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.dashboardprocessamento.ObtemTotalParcelasRejeitadasPeriodoQuery;

public class ObtemTotalParcelasRejeitadasPeriodoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemTotalParcelasRejeitadasPeriodoQuery query = new ObtemTotalParcelasRejeitadasPeriodoQuery();
        query.periodo = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.tipoEntidade = "ORG";
        query.codigoEntidade = "123";

        executarConsulta(query);
    }
}

