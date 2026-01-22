package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servico.ListaServicoNaturezaServicoQuery;

public class ListaServicoNaturezaServicoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaServicoNaturezaServicoQuery query = new ListaServicoNaturezaServicoQuery();
        query.nseCodigo = "123";
        query.svcIdentificador = "123";

        executarConsulta(query);
    }
}

