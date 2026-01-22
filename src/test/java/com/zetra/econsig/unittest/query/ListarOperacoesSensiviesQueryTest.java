package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.funcao.ListarOperacoesSensiviesQuery;

public class ListarOperacoesSensiviesQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListarOperacoesSensiviesQuery query = new ListarOperacoesSensiviesQuery();
        query.entidadeCodigo = "123";
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);
        query.count = false;

        executarConsulta(query);
    }
}

