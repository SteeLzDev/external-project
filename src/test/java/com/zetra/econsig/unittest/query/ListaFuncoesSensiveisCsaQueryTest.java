package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.funcao.ListaFuncoesSensiveisCsaQuery;

public class ListaFuncoesSensiveisCsaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaFuncoesSensiveisCsaQuery query = new ListaFuncoesSensiveisCsaQuery();

        executarConsulta(query);
    }
}

