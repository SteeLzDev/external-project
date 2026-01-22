package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoOrdenadaPorAdeDataQuery;

public class ListaConsignacaoOrdenadaPorAdeDataQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConsignacaoOrdenadaPorAdeDataQuery query = new ListaConsignacaoOrdenadaPorAdeDataQuery();
        query.tipoOrdenacao = "123";
        query.adeCodigos = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

