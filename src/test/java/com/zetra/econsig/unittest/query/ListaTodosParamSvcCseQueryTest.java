package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.parametro.ListaTodosParamSvcCseQuery;

public class ListaTodosParamSvcCseQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaTodosParamSvcCseQuery query = new ListaTodosParamSvcCseQuery();
        query.svcCodigo = "050E8080808080808080808080808280";
        query.responsavelAltera = "123";
        query.tpsCodigos = java.util.List.of("1", "2");
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);

        executarConsulta(query);
    }
}

