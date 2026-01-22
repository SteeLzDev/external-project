package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.parametro.ListaRelacionamentosQuery;

public class ListaRelacionamentosQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaRelacionamentosQuery query = new ListaRelacionamentosQuery();
        query.tntCodigo = "123";
        query.svcCodigoOrigem = "123";
        query.svcCodigoDestino = "123";

        executarConsulta(query);
    }
}

