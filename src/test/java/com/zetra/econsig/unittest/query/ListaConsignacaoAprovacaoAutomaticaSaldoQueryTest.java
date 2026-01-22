package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.compra.ListaConsignacaoAprovacaoAutomaticaSaldoQuery;

public class ListaConsignacaoAprovacaoAutomaticaSaldoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConsignacaoAprovacaoAutomaticaSaldoQuery query = new ListaConsignacaoAprovacaoAutomaticaSaldoQuery();

        executarConsulta(query);
    }
}

