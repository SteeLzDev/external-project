package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoSemDecisaoJudicialQuery;

public class ListaConsignacaoSemDecisaoJudicialQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConsignacaoSemDecisaoJudicialQuery query = new ListaConsignacaoSemDecisaoJudicialQuery();
        query.adeCodigos = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

