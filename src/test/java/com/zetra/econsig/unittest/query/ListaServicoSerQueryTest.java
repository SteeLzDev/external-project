package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servico.ListaServicoSerQuery;

public class ListaServicoSerQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaServicoSerQuery query = new ListaServicoSerQuery();
        query.rseCodigo = "123";
        query.csaCodigo = "267";
        query.nseCodigo = "123";
        query.ativos = true;
        query.count = false;

        executarConsulta(query);
    }
}

