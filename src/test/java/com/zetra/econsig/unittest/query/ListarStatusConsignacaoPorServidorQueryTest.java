package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ListarStatusConsignacaoPorServidorQuery;

public class ListarStatusConsignacaoPorServidorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListarStatusConsignacaoPorServidorQuery query = new ListarStatusConsignacaoPorServidorQuery();
        query.rseCodigo = "123";

        executarConsulta(query);
    }
}

