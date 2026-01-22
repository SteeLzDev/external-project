package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.parametro.ListaParamSistCseQuery;

public class ListaParamSistCseQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        com.zetra.econsig.helper.seguranca.AcessoSistema responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);

        ListaParamSistCseQuery query = new ListaParamSistCseQuery(responsavel);
        query.perCodigo = "123";
        query.tpcCseAltera = "123";
        query.tpcCseConsulta = "123";
        query.tpcSupAltera = "123";
        query.tpcSupConsulta = "123";

        executarConsulta(query);
    }
}

