package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.compra.ListaConsignacaoCancelamentoInfPgtSaldoQuery;

public class ListaConsignacaoCancelamentoInfPgtSaldoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConsignacaoCancelamentoInfPgtSaldoQuery query = new ListaConsignacaoCancelamentoInfPgtSaldoQuery();

        executarConsulta(query);
    }
}

