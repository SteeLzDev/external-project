package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.coeficiente.ListaCoeficienteQuery;

public class ListaCoeficienteQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaCoeficienteQuery query = new ListaCoeficienteQuery();
        query.tipo = "M";
        query.csaCodigo = "267";
        query.svcCodigo = "050E8080808080808080808080808280";
        query.prazo = 1;

        executarConsulta(query);
    }
}

