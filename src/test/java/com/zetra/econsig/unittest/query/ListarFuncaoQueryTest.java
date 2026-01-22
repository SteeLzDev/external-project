package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.funcao.ListarFuncaoQuery;

public class ListarFuncaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListarFuncaoQuery query = new ListarFuncaoQuery();
        query.count = false;
        query.funDescricao = "123";
        query.grfDescricao = "123";

        executarConsulta(query);
    }
}

