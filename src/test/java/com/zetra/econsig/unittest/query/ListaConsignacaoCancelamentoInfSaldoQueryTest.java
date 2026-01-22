package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.compra.ListaConsignacaoCancelamentoInfSaldoQuery;

public class ListaConsignacaoCancelamentoInfSaldoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConsignacaoCancelamentoInfSaldoQuery query = new ListaConsignacaoCancelamentoInfSaldoQuery();

        executarConsulta(query);
    }
}

