package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servico.ListaServicoComParametroQuery;

public class ListaServicoComParametroQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaServicoComParametroQuery query = new ListaServicoComParametroQuery();
        query.tpsCodigo = "123";
        query.orgCodigo = "751F8080808080808080808080809780";
        query.csaCodigo = "267";
        query.corCodigo = "EF128080808080808080808080809980";
        query.pseVlrs = java.util.List.of("1", "2");
        query.svcCodigo = "050E8080808080808080808080808280";
        query.selectNull = true;
        query.nseCodigo = "123";
        query.ativos = true;

        executarConsulta(query);
    }
}

