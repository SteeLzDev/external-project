package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.compra.ListaConsignacaoCancelamentoAprovacaoSaldoQuery;

public class ListaConsignacaoCancelamentoAprovacaoSaldoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConsignacaoCancelamentoAprovacaoSaldoQuery query = new ListaConsignacaoCancelamentoAprovacaoSaldoQuery();

        executarConsulta(query);
    }
}

