package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.prazo.ListaLimiteTaxaPorPrazoCsaQuery;

public class ListaLimiteTaxaPorPrazoCsaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaLimiteTaxaPorPrazoCsaQuery query = new ListaLimiteTaxaPorPrazoCsaQuery();
        query.csaCodigo = "267";
        query.svcCodigo = "050E8080808080808080808080808280";
        query.orgCodigo = "751F8080808080808080808080809780";
        query.prazo = 1;

        executarConsulta(query);
    }
}

