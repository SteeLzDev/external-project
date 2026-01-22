package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoReativacaoAutomaticaQuery;

public class ListaConsignacaoReativacaoAutomaticaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConsignacaoReativacaoAutomaticaQuery query = new ListaConsignacaoReativacaoAutomaticaQuery();

        executarConsulta(query);
    }
}

