package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servico.ListaServicoOrgQuery;

public class ListaServicoOrgQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaServicoOrgQuery query = new ListaServicoOrgQuery();
        query.orgCodigo = "751F8080808080808080808080809780";

        executarConsulta(query);
    }
}

