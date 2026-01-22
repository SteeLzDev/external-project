package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignataria.ListaCsaSolicitacaoLiquidacaoNaoConfirmadaQuery;

public class ListaCsaSolicitacaoLiquidacaoNaoConfirmadaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaCsaSolicitacaoLiquidacaoNaoConfirmadaQuery query = new ListaCsaSolicitacaoLiquidacaoNaoConfirmadaQuery();
        query.diasBloqueioNaoConfirmacao = 1;
        query.csaCodigo = "267";

        executarConsulta(query);
    }
}

