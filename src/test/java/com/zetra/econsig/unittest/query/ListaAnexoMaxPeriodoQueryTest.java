package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.anexo.ListaAnexoMaxPeriodoQuery;

public class ListaAnexoMaxPeriodoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaAnexoMaxPeriodoQuery query = new ListaAnexoMaxPeriodoQuery();
        query.tarCodigos = java.util.List.of("1", "2");
        query.count = false;

        executarConsulta(query);
    }
}

