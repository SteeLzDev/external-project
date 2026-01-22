package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.folha.ObtemTotalParcelasPeriodoProcessamentoQuery;

public class ObtemTotalParcelasPeriodoProcessamentoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemTotalParcelasPeriodoProcessamentoQuery query = new ObtemTotalParcelasPeriodoProcessamentoQuery();
        query.periodo = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.tipoEntidade = "ORG";
        query.codigoEntidade = "123";

        executarConsulta(query);
    }
}

