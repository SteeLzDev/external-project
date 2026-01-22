package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.funcao.ListaFuncoesRegraTaxaQuery;

public class ListaFuncoesRegraTaxaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaFuncoesRegraTaxaQuery query = new ListaFuncoesRegraTaxaQuery();
        query.funCodigos = "123";

        executarConsulta(query);
    }
}

