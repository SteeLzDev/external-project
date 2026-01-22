package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.convenio.ListaConvenioConsolidaDescontosQuery;

public class ListaConvenioConsolidaDescontosQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConvenioConsolidaDescontosQuery query = new ListaConvenioConsolidaDescontosQuery();
        query.svcCodigo = "050E8080808080808080808080808280";
        query.orgCodigo = "751F8080808080808080808080809780";

        executarConsulta(query);
    }
}

