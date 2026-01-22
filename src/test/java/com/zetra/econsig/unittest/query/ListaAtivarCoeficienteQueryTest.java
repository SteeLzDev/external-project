package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.coeficiente.ListaAtivarCoeficienteQuery;

public class ListaAtivarCoeficienteQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaAtivarCoeficienteQuery query = new ListaAtivarCoeficienteQuery();
        query.tipo = "M";
        query.csaCodigo = "267";
        query.svcCodigo = "050E8080808080808080808080808280";
        query.prazo = 1;
        query.filtro = 1;
        query.cftDataIniVig = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();

        executarConsulta(query);
    }
}

