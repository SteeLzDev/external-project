package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.orgao.ListaSubOrgaoQuery;

public class ListaSubOrgaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaSubOrgaoQuery query = new ListaSubOrgaoQuery();
        query.orgCodigo = "751F8080808080808080808080809780";
        query.sboIdentificador = "123";

        executarConsulta(query);
    }
}

