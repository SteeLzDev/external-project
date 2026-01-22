package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.subrelatorio.ListaSubrelatorioQuery;

public class ListaSubrelatorioQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaSubrelatorioQuery query = new ListaSubrelatorioQuery();
        query.sreCodigo = "123";
        query.relCodigo = "123";

        executarConsulta(query);
    }
}

