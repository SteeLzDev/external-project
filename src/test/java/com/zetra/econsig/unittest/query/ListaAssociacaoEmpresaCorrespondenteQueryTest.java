package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.correspondente.ListaAssociacaoEmpresaCorrespondenteQuery;

public class ListaAssociacaoEmpresaCorrespondenteQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaAssociacaoEmpresaCorrespondenteQuery query = new ListaAssociacaoEmpresaCorrespondenteQuery();
        query.count = false;
        query.ecoCodigo = "123";

        executarConsulta(query);
    }
}

