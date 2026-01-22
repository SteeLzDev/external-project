package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.coeficiente.ListaConsignatariaFiltroSimulacaoQuery;

public class ListaConsignatariaFiltroSimulacaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConsignatariaFiltroSimulacaoQuery query = new ListaConsignatariaFiltroSimulacaoQuery();
        query.svcCodigo = "050E8080808080808080808080808280";
        query.rseCodigo = "123";

        executarConsulta(query);
    }
}

