package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ListaStatusConsignacaoQuery;

public class ListaStatusConsignacaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaStatusConsignacaoQuery query = new ListaStatusConsignacaoQuery();
        query.sadCodigos = java.util.List.of("1", "2");
        query.filtraApenasSadExibeSim = true;

        executarConsulta(query);
    }
}

