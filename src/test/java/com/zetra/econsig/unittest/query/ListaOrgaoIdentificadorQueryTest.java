package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.orgao.ListaOrgaoIdentificadorQuery;

public class ListaOrgaoIdentificadorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaOrgaoIdentificadorQuery query = new ListaOrgaoIdentificadorQuery();
        query.orgCodigos = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

