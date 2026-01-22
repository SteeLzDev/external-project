package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servico.ListaNaturezaServicoQuery;

public class ListaNaturezaServicoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaNaturezaServicoQuery query = new ListaNaturezaServicoQuery();
        query.orderById = true;
        query.naturezaBeneficio = true;
        query.nseCodigoPai = "123";
        query.orgCodigo = "751F8080808080808080808080809780";
        query.nseCodigo = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

