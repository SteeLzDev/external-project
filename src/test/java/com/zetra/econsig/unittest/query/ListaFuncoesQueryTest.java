package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.funcao.ListaFuncoesQuery;

public class ListaFuncoesQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaFuncoesQuery query = new ListaFuncoesQuery();
        query.tipo = "123";

        executarConsulta(query);
    }
}

