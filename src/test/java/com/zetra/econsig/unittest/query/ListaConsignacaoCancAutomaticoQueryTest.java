package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoCancAutomaticoQuery;

public class ListaConsignacaoCancAutomaticoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConsignacaoCancAutomaticoQuery query = new ListaConsignacaoCancAutomaticoQuery();
        query.rseCodigo = "123";
        query.csaCodigo = "267";
        query.sadCodigos = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

