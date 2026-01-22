package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoLimitadorNovosAdesQuery;

public class ListaConsignacaoLimitadorNovosAdesQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConsignacaoLimitadorNovosAdesQuery query = new ListaConsignacaoLimitadorNovosAdesQuery();
        query.svcCodigo = "050E8080808080808080808080808280";
        query.rseCodigo = "123";
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);

        executarConsulta(query);
    }
}

