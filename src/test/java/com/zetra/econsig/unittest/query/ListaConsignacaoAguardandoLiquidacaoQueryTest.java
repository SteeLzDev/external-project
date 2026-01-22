package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoAguardandoLiquidacaoQuery;

public class ListaConsignacaoAguardandoLiquidacaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConsignacaoAguardandoLiquidacaoQuery query = new ListaConsignacaoAguardandoLiquidacaoQuery();
        query.rseCodigos = java.util.List.of("1", "2");
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);

        executarConsulta(query);
    }
}

