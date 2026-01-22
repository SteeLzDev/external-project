package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoInsereAlteraAConcluirQuery;

public class ListaConsignacaoInsereAlteraAConcluirQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConsignacaoInsereAlteraAConcluirQuery query = new ListaConsignacaoInsereAlteraAConcluirQuery();

        executarConsulta(query);
    }
}

