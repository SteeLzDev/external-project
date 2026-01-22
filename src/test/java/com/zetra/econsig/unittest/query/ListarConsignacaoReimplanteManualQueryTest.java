package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ListarConsignacaoReimplanteManualQuery;

public class ListarConsignacaoReimplanteManualQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListarConsignacaoReimplanteManualQuery query = new ListarConsignacaoReimplanteManualQuery();
        query.rseCodigo = "123";
        query.csaCodigo = "267";
        query.adeNumeros = java.util.List.of(1l, 2l);
        query.count = false;
        query.periodoAtual = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();

        executarConsulta(query);
    }
}

