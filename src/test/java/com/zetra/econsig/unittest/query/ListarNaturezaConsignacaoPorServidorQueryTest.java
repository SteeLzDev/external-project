package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ListarNaturezaConsignacaoPorServidorQuery;

public class ListarNaturezaConsignacaoPorServidorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListarNaturezaConsignacaoPorServidorQuery query = new ListarNaturezaConsignacaoPorServidorQuery();
        query.rseCodigo = "123";

        executarConsulta(query);
    }
}

