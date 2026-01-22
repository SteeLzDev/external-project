package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.compra.ListaConsignacaoLiquidacaoAutomaticaQuery;

public class ListaConsignacaoLiquidacaoAutomaticaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        int diasLiqAutomatica = 1;

        ListaConsignacaoLiquidacaoAutomaticaQuery query = new ListaConsignacaoLiquidacaoAutomaticaQuery(diasLiqAutomatica);

        executarConsulta(query);
    }
}

