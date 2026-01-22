package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoSemOcaNoPeriodoQuery;

public class ListaConsignacaoSemOcaNoPeriodoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConsignacaoSemOcaNoPeriodoQuery query = new ListaConsignacaoSemOcaNoPeriodoQuery();
        query.csaCodigo = "267";
        query.tocCodigos = java.util.List.of("1", "2");
        query.count = false;
        query.ocaPeriodo = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.sum = false;
        query.sadCodigos = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

