package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.convenio.ListaConvenioConsignatariaBloquearQuery;

public class ListaConvenioConsignatariaBloquearQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConvenioConsignatariaBloquearQuery query = new ListaConvenioConsignatariaBloquearQuery();
        query.csaCodigo = "267";
        query.orgCodigo = "751F8080808080808080808080809780";
        query.svcCodigo = "050E8080808080808080808080808280";

        executarConsulta(query);
    }
}

