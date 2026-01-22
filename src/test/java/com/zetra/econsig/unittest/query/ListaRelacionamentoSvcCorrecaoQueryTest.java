package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.parametro.ListaRelacionamentoSvcCorrecaoQuery;

public class ListaRelacionamentoSvcCorrecaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaRelacionamentoSvcCorrecaoQuery query = new ListaRelacionamentoSvcCorrecaoQuery();
        query.svcCodigoOrigem = "123";

        executarConsulta(query);
    }
}

