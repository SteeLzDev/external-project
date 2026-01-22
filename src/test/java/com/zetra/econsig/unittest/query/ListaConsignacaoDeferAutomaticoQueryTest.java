package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoDeferAutomaticoQuery;

public class ListaConsignacaoDeferAutomaticoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConsignacaoDeferAutomaticoQuery query = new ListaConsignacaoDeferAutomaticoQuery();

        executarConsulta(query);
    }
}

