package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.parametro.ListaServicosRelacionadosQuery;

public class ListaServicosRelacionadosQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaServicosRelacionadosQuery query = new ListaServicosRelacionadosQuery();
        query.svcCodigoOrigem = "123";
        query.tntCodigo = "123";

        executarConsulta(query);
    }
}

