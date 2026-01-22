package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.compra.ListaConsignacaoCancelamentoLiquidacaoQuery;

public class ListaConsignacaoCancelamentoLiquidacaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConsignacaoCancelamentoLiquidacaoQuery query = new ListaConsignacaoCancelamentoLiquidacaoQuery();

        executarConsulta(query);
    }
}

