package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.parametro.ListaServicoRelacionamentoSvcQuery;

public class ListaServicoRelacionamentoSvcQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaServicoRelacionamentoSvcQuery query = new ListaServicoRelacionamentoSvcQuery();
        query.svcCodigoOrigem = "123";
        query.tntCodigo = "123";

        executarConsulta(query);
    }
}

