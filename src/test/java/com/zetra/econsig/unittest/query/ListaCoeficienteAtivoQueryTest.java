package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.coeficiente.ListaCoeficienteAtivoQuery;

public class ListaCoeficienteAtivoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaCoeficienteAtivoQuery query = new ListaCoeficienteAtivoQuery();
        query.csaCodigo = "267";
        query.svcCodigo = "050E8080808080808080808080808280";
        query.orgCodigo = "751F8080808080808080808080809780";
        query.rseCodigo = "123";
        query.prazo = 1;
        query.dia = 1;
        query.validaBloqSerCnvCsa = true;

        executarConsulta(query);
    }
}

