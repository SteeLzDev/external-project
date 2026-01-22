package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.convenio.ListaOrgaoConvenioCorrespondenteQuery;

public class ListaOrgaoConvenioCorrespondenteQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaOrgaoConvenioCorrespondenteQuery query = new ListaOrgaoConvenioCorrespondenteQuery();
        query.corCodigo = "EF128080808080808080808080809980";
        query.svcCodigo = "050E8080808080808080808080808280";

        executarConsulta(query);
    }
}

