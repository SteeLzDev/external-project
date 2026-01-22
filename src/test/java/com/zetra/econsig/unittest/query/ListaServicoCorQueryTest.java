package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servico.ListaServicoCorQuery;

public class ListaServicoCorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaServicoCorQuery query = new ListaServicoCorQuery();
        query.corCodigo = "EF128080808080808080808080809980";

        executarConsulta(query);
    }
}

