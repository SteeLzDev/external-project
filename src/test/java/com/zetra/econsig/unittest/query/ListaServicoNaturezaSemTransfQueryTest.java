package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servico.ListaServicoNaturezaSemTransfQuery;

public class ListaServicoNaturezaSemTransfQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaServicoNaturezaSemTransfQuery query = new ListaServicoNaturezaSemTransfQuery();
        query.count = false;

        executarConsulta(query);
    }
}

