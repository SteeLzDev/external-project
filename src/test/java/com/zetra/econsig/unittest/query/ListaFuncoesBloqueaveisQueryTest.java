package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.funcao.ListaFuncoesBloqueaveisQuery;

public class ListaFuncoesBloqueaveisQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaFuncoesBloqueaveisQuery query = new ListaFuncoesBloqueaveisQuery();
        query.tipo = "123";

        executarConsulta(query);
    }
}

