package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.coeficiente.ListaServicoPrazoAtivoQuery;

public class ListaServicoPrazoAtivoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaServicoPrazoAtivoQuery query = new ListaServicoPrazoAtivoQuery();
        query.csaCodigo = "267";
        query.svcCodigo = "050E8080808080808080808080808280";
        query.prazoInicial = "123";
        query.prazoFinal = "123";
        query.prazoOrdenacao = "123";
        query.prazosInformados = "123";
        // query.prazosInformadosList = java.util.List.of((short) 1, (short) 2);
        query.prazo = true;
        query.prazoMultiploDoze = true;

        executarConsulta(query);
    }
}

