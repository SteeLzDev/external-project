package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.convenio.ListaConveniosParaAlteracaoQuery;

public class ListaConveniosParaAlteracaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConveniosParaAlteracaoQuery query = new ListaConveniosParaAlteracaoQuery();
        query.svcCodigo = "050E8080808080808080808080808280";
        query.csaCodigo = "267";
        query.orgCodigo = "751F8080808080808080808080809780";
        query.codigos = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

