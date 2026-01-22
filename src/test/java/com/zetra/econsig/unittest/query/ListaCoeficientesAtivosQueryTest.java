package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.coeficiente.ListaCoeficientesAtivosQuery;

public class ListaCoeficientesAtivosQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaCoeficientesAtivosQuery query = new ListaCoeficientesAtivosQuery();
        query.csaCodigo = "267";
        query.svcCodigo = "050E8080808080808080808080808280";
        query.possuiDataFim = true;

        executarConsulta(query);
    }
}

