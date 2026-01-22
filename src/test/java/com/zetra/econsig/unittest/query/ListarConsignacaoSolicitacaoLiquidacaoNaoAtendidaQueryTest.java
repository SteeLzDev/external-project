package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ListarConsignacaoSolicitacaoLiquidacaoNaoAtendidaQuery;

public class ListarConsignacaoSolicitacaoLiquidacaoNaoAtendidaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListarConsignacaoSolicitacaoLiquidacaoNaoAtendidaQuery query = new ListarConsignacaoSolicitacaoLiquidacaoNaoAtendidaQuery();
        query.csaCodigo = "267";
        query.count = false;

        executarConsulta(query);
    }
}

