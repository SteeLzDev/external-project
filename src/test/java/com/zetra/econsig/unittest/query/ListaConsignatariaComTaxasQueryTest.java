package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.coeficiente.ListaConsignatariaComTaxasQuery;

public class ListaConsignatariaComTaxasQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConsignatariaComTaxasQuery query = new ListaConsignatariaComTaxasQuery();
        query.svcCodigo = "050E8080808080808080808080808280";

        executarConsulta(query);
    }
}

