package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.orgao.ListaOcorrenciaOrgaoQuery;

public class ListaOcorrenciaOrgaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaOcorrenciaOrgaoQuery query = new ListaOcorrenciaOrgaoQuery();
        query.count = false;
        query.orgCodigo = "751F8080808080808080808080809780";
        query.tocCodigo = "123";

        executarConsulta(query);
    }
}

