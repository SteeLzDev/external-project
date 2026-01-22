package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servico.ListaServicoCancelamentoAutomaticoQuery;

public class ListaServicoCancelamentoAutomaticoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaServicoCancelamentoAutomaticoQuery query = new ListaServicoCancelamentoAutomaticoQuery();
        query.orgCodigo = "751F8080808080808080808080809780";
        query.csaCodigo = "267";
        query.nseCodigo = "123";

        executarConsulta(query);
    }
}

