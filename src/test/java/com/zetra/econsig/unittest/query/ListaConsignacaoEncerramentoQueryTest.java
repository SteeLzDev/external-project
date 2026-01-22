package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoEncerramentoQuery;

public class ListaConsignacaoEncerramentoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        String rseCodigo = "123";

        ListaConsignacaoEncerramentoQuery query = new ListaConsignacaoEncerramentoQuery(rseCodigo);

        executarConsulta(query);
    }
}

