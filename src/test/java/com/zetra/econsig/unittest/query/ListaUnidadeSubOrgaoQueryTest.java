package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.orgao.ListaUnidadeSubOrgaoQuery;

public class ListaUnidadeSubOrgaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaUnidadeSubOrgaoQuery query = new ListaUnidadeSubOrgaoQuery();

        executarConsulta(query);
    }
}

