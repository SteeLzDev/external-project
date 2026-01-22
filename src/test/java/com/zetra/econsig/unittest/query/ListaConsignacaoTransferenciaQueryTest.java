package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoTransferenciaQuery;

public class ListaConsignacaoTransferenciaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConsignacaoTransferenciaQuery query = new ListaConsignacaoTransferenciaQuery();
        query.adeNumero = "123";
        query.rseCodigo = "123";
        query.sadCodigos = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

