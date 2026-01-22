package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.parametro.ListaOcorrenciaParamSistCseQuery;

public class ListaOcorrenciaParamSistCseQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaOcorrenciaParamSistCseQuery query = new ListaOcorrenciaParamSistCseQuery();
        query.tpcDescricao = "123";
        query.tpcCodigo = "123";
        query.usuLogin = "123";
        query.count = false;

        executarConsulta(query);
    }
}

