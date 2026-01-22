package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.parametro.ListaParamOrgaoQuery;

public class ListaParamOrgaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaParamOrgaoQuery query = new ListaParamOrgaoQuery();
        query.estCodigo = "751F8080808080808080808080809680";
        query.orgCodigo = "751F8080808080808080808080809780";
        query.taoCodigo = "123";
        query.paoVlr = "123";
        query.count = false;

        executarConsulta(query);
    }
}

