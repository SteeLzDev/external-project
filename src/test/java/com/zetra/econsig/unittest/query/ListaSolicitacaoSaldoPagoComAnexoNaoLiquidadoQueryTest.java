package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.saldodevedor.ListaSolicitacaoSaldoPagoComAnexoNaoLiquidadoQuery;

public class ListaSolicitacaoSaldoPagoComAnexoNaoLiquidadoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaSolicitacaoSaldoPagoComAnexoNaoLiquidadoQuery query = new ListaSolicitacaoSaldoPagoComAnexoNaoLiquidadoQuery();
        query.csaCodigo = "267";

        executarConsulta(query);
    }
}

