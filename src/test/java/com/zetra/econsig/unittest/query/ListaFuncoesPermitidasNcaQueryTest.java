package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.funcao.ListaFuncoesPermitidasNcaQuery;

public class ListaFuncoesPermitidasNcaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaFuncoesPermitidasNcaQuery query = new ListaFuncoesPermitidasNcaQuery();
        query.ncaCodigo = "1";

        executarConsulta(query);
    }
}

