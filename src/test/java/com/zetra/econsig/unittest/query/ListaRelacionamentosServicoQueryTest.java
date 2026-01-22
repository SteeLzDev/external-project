package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servico.ListaRelacionamentosServicoQuery;

public class ListaRelacionamentosServicoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaRelacionamentosServicoQuery query = new ListaRelacionamentosServicoQuery();
        query.svcCodigoOrigem = null;
        query.svcCodigoDestino = null;
        query.tntCodigo = null;
        query.count = false;

        executarConsulta(query);
    }
}

