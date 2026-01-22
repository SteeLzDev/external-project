package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoAguardandoMargemQuery;

public class ListaConsignacaoAguardandoMargemQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConsignacaoAguardandoMargemQuery query = new ListaConsignacaoAguardandoMargemQuery();
        query.rseCodigo = "123";

        executarConsulta(query);
    }
}

