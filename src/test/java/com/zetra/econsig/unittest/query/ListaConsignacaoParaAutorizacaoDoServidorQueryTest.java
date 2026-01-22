package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoParaAutorizacaoDoServidorQuery;

public class ListaConsignacaoParaAutorizacaoDoServidorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        ListaConsignacaoParaAutorizacaoDoServidorQuery query = new ListaConsignacaoParaAutorizacaoDoServidorQuery();
        query.rseCodigo = "123";
        executarConsulta(query);
    }
}
