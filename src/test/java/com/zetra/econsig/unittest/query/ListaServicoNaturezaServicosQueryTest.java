package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servico.ListaServicoNaturezaServicosQuery;

public class ListaServicoNaturezaServicosQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaServicoNaturezaServicosQuery query = new ListaServicoNaturezaServicosQuery();
        query.nseCodigos = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

